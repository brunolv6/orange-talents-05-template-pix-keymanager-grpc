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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BcbCriarPixResponse

        if (keyType != other.keyType) return false
        if (key != other.key) return false
        if (bankAccount != other.bankAccount) return false
        if (owner != other.owner) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + bankAccount.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }


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