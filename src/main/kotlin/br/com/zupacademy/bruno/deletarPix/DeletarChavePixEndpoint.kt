package br.com.zupacademy.bruno.deletarPix

import br.com.zupacademy.bruno.DeletarChavePixRequest
import br.com.zupacademy.bruno.DeletarChavePixResponse
import br.com.zupacademy.bruno.DeletarChavePixServiceGrpc
import br.com.zupacademy.bruno.compartilhados.erros.ErrorAroundHandler
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.ConstraintViolationExceptionSpecial
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@ErrorAroundHandler
open class DeletarChavePixEndpoint(
    val gerenciarRemocaoChavePix: GerenciarRemocaoChavePix,
    val validador: Validator,
): DeletarChavePixServiceGrpc.DeletarChavePixServiceImplBase() {

    override fun excluir(
        request: DeletarChavePixRequest,
        responseObserver: StreamObserver<DeletarChavePixResponse>
    ) {

        // criar objeto pra requisição
        val requestDeletarChave = request.toModel()

        // valida dados de entrada
        validador.validate(requestDeletarChave).run {
            if(this.isNotEmpty()) throw ConstraintViolationExceptionSpecial("Apresentou erros de validacao genericos", this)
        }

        // deletar pix do bcb e posteriormente do sistema interno
        gerenciarRemocaoChavePix.processa(requestDeletarChave)

        // retornar ao cliente que foi deletado
        responseObserver.onNext(DeletarChavePixResponse.newBuilder()
            .setRemovido(true)
            .build())

        responseObserver.onCompleted()
    }
}

fun DeletarChavePixRequest.toModel() = RequestDeletarChave(pixId.toLong(), clienteId)