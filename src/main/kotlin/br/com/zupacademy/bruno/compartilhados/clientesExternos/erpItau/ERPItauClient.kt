package br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau

import br.com.zupacademy.bruno.compartilhados.clientesExternos.ItauContaResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.ErrorHandlerBcbClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091/")
interface ERPItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun consultarConta(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<ItauContaResponse>
}
