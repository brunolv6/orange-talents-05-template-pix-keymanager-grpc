package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.compartilhados.validadores.isUUID
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class RequestChavePixExterna(
    @field:NotNull @field:NotBlank @field:isUUID val idCliente: String,
    @field:NotNull @field:NotBlank val tipoChave: TipoDeChave,
    @field:NotNull @field:NotBlank @field:Size(max = 77) val chave: String,
    @field:NotNull @field:NotBlank val tipoConta: TipoDeConta
){

}
