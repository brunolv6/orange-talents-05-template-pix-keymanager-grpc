package br.com.zupacademy.bruno.compartilhados.clientesExternos

import br.com.zupacademy.bruno.criarPix.RequestChave

class BcbCriarPixRequest(itauContaResponse: ItauContaResponse, requestChave: RequestChave, contaBanco: BankAccount, donoConta: Owner) {
    val keyType: String = requestChave.tipoChave.name
    val key: String = requestChave.chave
    val bankAccount: BankAccount = contaBanco
    val owner: Owner = donoConta
}

class Owner(itauContaResponse: ItauContaResponse) {
    val type: String = "NATURAL_PERSON"
    val name: String = itauContaResponse.titular.nome
    val taxIdNumber: String = itauContaResponse.titular.cpf
}

class BankAccount(itauContaResponse: ItauContaResponse) {
    val participant: String = itauContaResponse.instituicao.ispb
    val branch: String = itauContaResponse.agencia
    val accountNumber: String = itauContaResponse.numero
    val accountType: String = "CACC"
}
