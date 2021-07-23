package br.com.zupacademy.bruno.criarPix.entidades

import javax.persistence.Embeddable

@Embeddable
class ContaItau(
    val nome: String,
    val cpf: String,
    val instituicaoId: String,
    val agencia: String,
    val numeroConta: String,
    val idConta: String
)
