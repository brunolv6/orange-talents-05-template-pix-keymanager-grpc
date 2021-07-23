package br.com.zupacademy.bruno.criarPix

import javax.persistence.Embeddable

@Embeddable
class ContaItau(
    val nome: String,
    val cpf: String,
    val instituicaoId: String,
    val agencia: String,
    val numeroConta: String
)
