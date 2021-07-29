package br.com.zupacademy.bruno.criarPix.entidades

import br.com.zupacademy.bruno.criarPix.entidades.ContaItau
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class ChavePix(
    @field:NotBlank @field:NotNull val tipoDaChave: String,
    @field:NotBlank @field:NotNull @field:Size(max = 77) val chave: String,
    @field:Valid @Embedded val conta: ContaItau,
    @field:NotBlank @field:NotNull val tipoDaConta: String
) {

    @Id
    @GeneratedValue
    val idPix: Long? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

}
