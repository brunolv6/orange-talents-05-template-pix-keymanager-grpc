package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix

import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.entidades.ContaItau

class BcbCriarPixResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
) {
    fun toModel(idConta: String): ChavePix {

        val contaItau: ContaItau = ContaItau(nome = owner.name, cpf = owner.taxIdNumber, instituicaoId = bankAccount.participant, agencia = bankAccount.branch, numeroConta = bankAccount.accountNumber, idConta = idConta)

        return ChavePix(tipoDaChave = keyType, chave = key, conta = contaItau, tipoDaConta = bankAccount.accountType)
    }

    data class BankAccountResponse(
        val participant: String,
        val branch: String,
        val accountNumber: String,
        val accountType: String
    )

    data class OwnerResponse(
        val type: String,
        val name: String,
        val taxIdNumber: String
    )
}
