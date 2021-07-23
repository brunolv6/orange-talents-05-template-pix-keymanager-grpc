package br.com.zupacademy.bruno.compartilhados.clientesExternos

import br.com.zupacademy.bruno.criarPix.RequestChavePixExterna

class BcbCriarPixRequest(itauContaResponse: ItauContaResponse, requestChave: RequestChavePixExterna, contaBanco: BankAccount, donoConta: Owner) {
    val keyType: String = requestChave.tipoChave.toModeltoBcb()
    val key: String = requestChave.chave
    val bankAccount: BankAccount = contaBanco
    val owner: Owner = donoConta
}

class Owner(itauContaResponse: ItauContaResponse) {
    val type: String = "NATURAL_PERSON"
    val name: String = itauContaResponse.titular.nome
    val taxIdNumber: String = itauContaResponse.titular.cpf
}

class BankAccount(itauContaResponse: ItauContaResponse, accountType: String) {
    val participant: String = itauContaResponse.instituicao.ispb
    val branch: String = itauContaResponse.agencia
    val accountNumber: String = itauContaResponse.numero
    val accountType: String = accountType
}
