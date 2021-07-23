package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb

import br.com.zupacademy.bruno.compartilhados.clientesExternos.BcbCriarPixRequest
import br.com.zupacademy.bruno.compartilhados.erros.ErrorAroundHandler
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@ErrorHandlerBcbClient
@Client("http://localhost:8082/")
interface BcbClient {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun criarChavePix(@Body bcbCriarPixRequest: BcbCriarPixRequest): HttpResponse<BcbCriarPixResponse>
}