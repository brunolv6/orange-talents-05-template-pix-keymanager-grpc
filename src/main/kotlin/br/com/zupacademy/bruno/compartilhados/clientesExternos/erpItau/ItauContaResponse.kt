package br.com.zupacademy.bruno.compartilhados.clientesExternos


data class ItauContaResponse(
    val tipo: String,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {

}

data class Instituicao(
    val nome: String,
    val ispb: String
)

data class Titular(
    val id: String,
    val nome: String,
    val cpf: String
)
