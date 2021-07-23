package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.TipoChave
import br.com.zupacademy.bruno.TipoConta
import br.com.zupacademy.bruno.compartilhados.validadores.ChavePixValida
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class RequestChave(
    @field:NotNull @field:NotBlank val idCliente: String,
    @field:NotNull @field:NotBlank  val tipoChave: TipoChave,
    @field:NotNull @field:NotBlank val chave: String,
    @field:NotNull @field:NotBlank val tipoConta: TipoConta
){

}
