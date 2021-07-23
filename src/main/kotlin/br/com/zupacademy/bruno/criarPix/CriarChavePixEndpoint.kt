package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.ChavePixServiceGrpc
import br.com.zupacademy.bruno.ChavePixServiceRequest
import br.com.zupacademy.bruno.ChavePixServiceResponse
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.ConstraintViolationExceptionSpecial
import br.com.zupacademy.bruno.compartilhados.erros.ErrorAroundHandler
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.AlreadyExistsErrorException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.InconsistenciaTipoChaveEChaveException
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.Validator


@Singleton // @Inject val valida: Valida
@ErrorAroundHandler
// @ChavePixValida
open class CriarChavePixEndpoint(
    val validador: Validator,
    val chavePixRepository: ChavePixRepository,
    val criaChavePixExterna: CriaChavePixExterna
): ChavePixServiceGrpc.ChavePixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastrar(
        request: ChavePixServiceRequest,
        responseObserver: StreamObserver<ChavePixServiceResponse>
    ) {

        // validar dados da requisicao e atualizar para estado interno
        val requestChavePix: RequestChavePixExterna = validarDados(request)

        // registrar chave em sistema externo
        val chavePix: ChavePix = criaChavePixExterna.registrar(requestChavePix)

        chavePixRepository.save(chavePix)

        logger.info("Chave Pix de id ${chavePix.idPix} persistida em sistema interno")

        val response = ChavePixServiceResponse.newBuilder().setPixId(chavePix.idPix.toString()).build()

        responseObserver.onNext(response)

        responseObserver.onCompleted()

    }

    private fun validarDados(request: ChavePixServiceRequest): RequestChavePixExterna {

        val tipoDeChave: TipoDeChave = TipoDeChave.valueOf(request.tipoChave.name)

        if(!tipoDeChave.valida(request.chave)) throw InconsistenciaTipoChaveEChaveException("Apresentou erros de validacao")

        if(chavePixRepository.existeChaveEIdConta(request.chave, request.idCliente) > 0) throw AlreadyExistsErrorException("Relacao chave pix e cliente já existente")

        logger.info("Tipo de chave e chave pix são validas segundo sistema interno")

        val requestChavePix = request.toModel(tipoDeChave)

        validador.validate(requestChavePix).run {
            if(this.isNotEmpty()) throw ConstraintViolationExceptionSpecial("Apresentou erros de validacao", this)
        }

        logger.info("Dados de entrada válidados e transformados para tipo interno")

        return requestChavePix

    }


}