package br.com.zupacademy.bruno.compartilhados.clientesExternos

import br.com.zupacademy.bruno.criarPix.RequestChavePixExterna

class BcbCriarPixRequest(itauContaResponse: ItauContaResponse, requestChave: RequestChavePixExterna, contaBanco: BankAccount, donoConta: Owner) {
    val keyType: String = requestChave.tipoChave.toModeltoBcb()
    val key: String = requestChave.chave
    val bankAccount: BankAccount = contaBanco
    val owner: Owner = donoConta

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BcbCriarPixRequest

        if (keyType != other.keyType) return false
        if (key != other.key) return false
        if (bankAccount != other.bankAccount) return false
        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + bankAccount.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }


}

class Owner(itauContaResponse: ItauContaResponse) {
    val type: String = "NATURAL_PERSON"
    val name: String = itauContaResponse.titular.nome
    val taxIdNumber: String = itauContaResponse.titular.cpf
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Owner

        if (type != other.type) return false
        if (name != other.name) return false
        if (taxIdNumber != other.taxIdNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + taxIdNumber.hashCode()
        return result
    }


}

class BankAccount(itauContaResponse: ItauContaResponse, accountType: String) {
    val participant: String = itauContaResponse.instituicao.ispb
    val branch: String = itauContaResponse.agencia
    val accountNumber: String = itauContaResponse.numero
    val accountType: String = accountType
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BankAccount

        if (participant != other.participant) return false
        if (branch != other.branch) return false
        if (accountNumber != other.accountNumber) return false
        if (accountType != other.accountType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = participant.hashCode()
        result = 31 * result + branch.hashCode()
        result = 31 * result + accountNumber.hashCode()
        result = 31 * result + accountType.hashCode()
        return result
    }


}
