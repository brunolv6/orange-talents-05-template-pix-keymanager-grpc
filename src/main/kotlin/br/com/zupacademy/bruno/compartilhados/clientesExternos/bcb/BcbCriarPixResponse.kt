package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb

import br.com.zupacademy.bruno.criarPix.ChavePix
import br.com.zupacademy.bruno.criarPix.ContaItau

class BcbCriarPixResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
) {
    fun toModel(): ChavePix {

        val contaItau: ContaItau = ContaItau(nome = owner.name, cpf = owner.taxIdNumber, instituicaoId = bankAccount.participant, agencia = bankAccount.branch, numeroConta = bankAccount.accountNumber)

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
