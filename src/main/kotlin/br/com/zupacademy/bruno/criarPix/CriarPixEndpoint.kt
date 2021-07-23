package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.ChavePixServiceGrpc
import br.com.zupacademy.bruno.ChavePixServiceRequest
import br.com.zupacademy.bruno.ChavePixServiceResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.*
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbCriarPixResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.ERPItauClient
import br.com.zupacademy.bruno.compartilhados.erros.BadRequestErrorException
import br.com.zupacademy.bruno.compartilhados.erros.ConstraintViolationExceptionSpecial
import br.com.zupacademy.bruno.compartilhados.erros.ErrorAroundHandler
import br.com.zupacademy.bruno.compartilhados.erros.InconsistenciaTipoChaveEChaveException
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.Validator


@Singleton // @Inject val valida: Valida
@ErrorAroundHandler
// @ChavePixValida
open class CriarPixEndpoint(
    val validador: Validator,
    val erpItauClient: ERPItauClient,
    val bcbClient: BcbClient,
    val chavePixRepository: ChavePixRepository
): ChavePixServiceGrpc.ChavePixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastrar(
        request: ChavePixServiceRequest,
        responseObserver: StreamObserver<ChavePixServiceResponse>
    ) {

        // 1. validar entrada de dados externos

        val requestChave = RequestChave(request.idCliente, request.tipoChave, request.chave, request.tipoConta)

        val erros = validador.validate(requestChave)

        if(!erros.isEmpty()) throw ConstraintViolationExceptionSpecial("Apresentou erros de validacao", erros)

        val tipoDeChave: TipoDeChave = TipoDeChave.valueOf(request.tipoChave.name)

        if(!tipoDeChave.valida(request.chave)) throw InconsistenciaTipoChaveEChaveException("Apresentou erros de validacao")

        // 3. verificar se chave já existe no sistema interno

        // 4. pegar dados do ERP ITAU e tratar possíveis erros

        val contaItauResponse = erpItauClient.consultarConta(request.idCliente, request.tipoConta.name).body() ?: throw BadRequestErrorException("Conta não encontrada!")

        // 4.5 validar dados ITAU

        // 5. tentar criar chave pix no BCB e tratar possíveis erros
        val donoConta = Owner(contaItauResponse)

        val contaBanco = BankAccount(contaItauResponse)

        val bcbCriarPixRequest = BcbCriarPixRequest(contaItauResponse, requestChave, contaBanco, donoConta)

        val chavePixBcbReponse = bcbClient.criarChavePix(bcbCriarPixRequest).body()

        // 5.5 validar dados BCB

        // 6. modelar dados para domínio PIX após ter criado o domínio

        val chavePix: ChavePix = chavePixBcbReponse.toModel()
//
//        // 7. persistir registro no banco de dados
//
        chavePixRepository.save(chavePix)
//
//        // 8. retornar pixId para cliente
//
        val response = ChavePixServiceResponse.newBuilder().setPixId(chavePix.idPix.toString()).build()

        // 9. fechar conexão do gRPC
        // val response = ChavePixServiceResponse.newBuilder().setPixId("2").build()

        responseObserver.onNext(response)

        responseObserver.onCompleted()

    }
}