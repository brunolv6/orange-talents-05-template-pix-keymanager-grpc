package br.com.zupacademy.bruno.compartilhados.clientesExternos

import br.com.zupacademy.bruno.criarPix.RequestChavePixExterna
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta


data class ItauContaResponse(
    val tipo: String,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {
    fun toModel(request: RequestChavePixExterna): BcbCriarPixRequest {

        val contaBanco = BankAccount(this, TipoDeConta.valueOf(tipo).toModeltoBcb())

        val dono = Owner(this)

        return BcbCriarPixRequest(this, request, contaBanco, dono)
    }

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
