package br.com.zupacademy.bruno.criarPix.entidades

import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Embeddable
class ContaItau(
    @field:NotBlank @field:NotNull val nome: String,
    @field:NotBlank @field:NotNull  val cpf: String,
    @field:NotBlank @field:NotNull  val instituicaoId: String,
    @field:NotBlank @field:NotNull  val agencia: String,
    @field:NotBlank @field:NotNull  val numeroConta: String,
    @field:NotBlank @field:NotNull  val idConta: String
)
