package br.com.zupacademy.bruno.listarPix

import br.com.zupacademy.bruno.compartilhados.validadores.isUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class RequestLista(
    @field:NotNull @field:NotBlank @field:isUUID val clientId: String
)
