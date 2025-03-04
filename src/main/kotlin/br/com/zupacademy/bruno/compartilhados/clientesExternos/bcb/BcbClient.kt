package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb

import br.com.zupacademy.bruno.compartilhados.clientesExternos.BcbCriarPixRequest
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.BcbCriarPixResponse
// import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.errorHandler.ErrorHandlerBcbClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

//@ErrorHandlerBcbClient
@Client("\${host.bcb.pix.url}")
interface BcbClient {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun criarChavePix(@Body bcbCriarPixRequest: BcbCriarPixRequest): HttpResponse<BcbCriarPixResponse>

    @Get("/api/v1/pix/keys/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun buscarChavePix(@PathVariable(name = "key") key: String): HttpResponse<BcbCriarPixResponse>

    @Delete("/api/v1/pix/keys/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun deletarChavePix(@PathVariable(name = "key") key: String, @Body keyBody: BcbDeleteRequest): HttpResponse<Any?>
}