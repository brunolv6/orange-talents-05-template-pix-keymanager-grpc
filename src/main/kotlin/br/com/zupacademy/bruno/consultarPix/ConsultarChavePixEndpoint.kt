package br.com.zupacademy.bruno.consultarPix

import br.com.zupacademy.bruno.ConsultarChavePixReponse
import br.com.zupacademy.bruno.ConsultarChavePixRequest
import br.com.zupacademy.bruno.ConsultarChavePixServiceGrpc
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.erros.ErrorAroundHandler
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.BadRequestErrorException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.ConstraintViolationExceptionSpecial
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import io.grpc.stub.StreamObserver
import io.micronaut.http.annotation.Get
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Singleton
@ErrorAroundHandler
class ConsultarChavePixEndpoint(
    val validador: Validator,
    val repository: ChavePixRepository,
    val bcbClient: BcbClient
): ConsultarChavePixServiceGrpc.ConsultarChavePixServiceImplBase() {

    @Get
    override fun consultar(
        request: ConsultarChavePixRequest,
        responseObserver: StreamObserver<ConsultarChavePixReponse>
    ) {

        // validar e transformar em um super enum de request validado
        val requestValidada =  request.toModel(validador)

        // fazer consulta
        val dadosChavePixResponse = requestValidada.encontrarDadosPix(repository, bcbClient)

        // retornar consulta ao cliente e fechar conexão
        responseObserver.onNext(dadosChavePixResponse.toModelGrpc())
        responseObserver.onCompleted()
    }
}

fun ConsultarChavePixRequest.toModel(validador: Validator): RequestValidada{
    val requestValidado = when (tipoConsultaCase) {
        ConsultarChavePixRequest.TipoConsultaCase.SISTEMAINTERNO -> RequestValidada.SistemaInterno(sistemaInterno.pixId, sistemaInterno.clientId)
        ConsultarChavePixRequest.TipoConsultaCase.VALORCHAVE -> RequestValidada.ValorChave(valorChave)
        ConsultarChavePixRequest.TipoConsultaCase.TIPOCONSULTA_NOT_SET -> throw BadRequestErrorException("Tipo de requisicao nao suportada")
    }

    validador.validate(requestValidado).run {
        if(this.isNotEmpty()) throw ConstraintViolationExceptionSpecial("Dados de entrada invalidos", this)
    }

    return requestValidado
}