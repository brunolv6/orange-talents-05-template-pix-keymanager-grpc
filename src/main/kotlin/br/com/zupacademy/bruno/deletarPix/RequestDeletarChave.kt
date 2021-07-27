package br.com.zupacademy.bruno.deletarPix

import br.com.zupacademy.bruno.compartilhados.validadores.isUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RequestDeletarChave(
    @field:NotBlank val pixId: Long?,
    @field: isUUID @field:NotBlank val clienteId: String?
) {

}
