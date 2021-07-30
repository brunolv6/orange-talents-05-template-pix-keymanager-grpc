package br.com.zupacademy.bruno.listarPix

import br.com.zupacademy.bruno.*
import br.com.zupacademy.bruno.compartilhados.erros.ErrorAroundHandler
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.BadRequestErrorException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.ConstraintViolationExceptionSpecial
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.NotFoundException
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import br.com.zupacademy.bruno.criarPix.toModel
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import org.hibernate.exception.ConstraintViolationException
import java.time.ZoneId
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@ErrorAroundHandler
class ListarChavePix(
    val validator: Validator,
    val repository: ChavePixRepository
): ListarChavePixServiceGrpc.ListarChavePixServiceImplBase() {

    override fun listar(
        request: ListarChavePixRequest,
        responseObserver: StreamObserver<ListarChavePixResponse>
    ) {

        // validar dados entrada
        val requestLista = request.toModel()

        validator.validate(requestLista).run {
            if(this.isNotEmpty()) throw ConstraintViolationExceptionSpecial("Apresentou erros de validacao genericos", this)
        }

        // buscar lista de pix e transformar
        val listaChavePix: List<ListarChavePixResponse.ChavePix> =  repository.findByContaIdConta(requestLista.clientId).map{
            val tipoConta = when(it.tipoDaConta){
                "CACC" -> "CONTA_CORRENTE"
                else -> "CONTA_POUPANCA"
            }
            ListarChavePixResponse.ChavePix.newBuilder()
                .setChave(it.chave)
                .setPixId(it.idPix.toString())
                .setTipoChave(TipoChave.valueOf(toModeltoEnum(it.tipoDaChave)))
                .setTipoConta(TipoConta.valueOf(tipoConta))
                .setDataCriacao(it.criadaEm.let{
                    val dataCadastro = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder().setSeconds(dataCadastro.epochSecond)
                        .setNanos(dataCadastro.nano).build()
                })
                .build()
        }

        // enviar resposta e desligar conexao
        responseObserver.onNext(ListarChavePixResponse.newBuilder()
            .setClienteId(requestLista.clientId)
            .addAllChavePix(listaChavePix)
            .build())

        responseObserver.onCompleted()
    }
}

fun ListarChavePixRequest.toModel(): RequestLista{
    return RequestLista(this.clientId)
}

fun toModeltoEnum(tipo: String): String{
    return when(tipo){
        "CPF"-> "CPF"
        "RANDOM" -> "ALEATORIO"
        "PHONE" -> "CELULAR"
        "EMAIL" -> "EMAIL"
        else -> throw BadRequestErrorException("Tipo Chave nao encontrado internamente")
    }
}