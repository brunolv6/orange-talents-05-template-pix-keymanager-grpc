package br.com.zupacademy.bruno.utils

import br.com.zupacademy.bruno.TipoChave
import br.com.zupacademy.bruno.TipoConta
import br.com.zupacademy.bruno.compartilhados.clientesExternos.*
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.BankAccountResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.BcbCriarPixResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.OwnerResponse
import br.com.zupacademy.bruno.criarPix.RequestChavePixExterna
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta

public fun itauContaResponseFake(): ItauContaResponse {
    return ItauContaResponse(
        tipo = "CONTA_CORRENTE",
        instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190"),
        agencia = "0001",
        numero = "291900",
        titular = Titular("c56dfef4-7901-44fb-84e2-a2cefb157890", "Rafael M C Ponte", "02467781054")
    )
}

public fun bcbCriarPixRequestFake(itauContaResponse: ItauContaResponse): BcbCriarPixRequest {
    return BcbCriarPixRequest(
        itauContaResponse = itauContaResponse,
        requestChave = RequestChavePixExterna(
            idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoDeChave.valueOf(TipoChave.CELULAR.name),
            chave = "+5511964044444",
            tipoConta = TipoDeConta.valueOf(TipoConta.CONTA_CORRENTE.name)
        ),
        contaBanco = BankAccount(
            itauContaResponse = itauContaResponse,
            accountType = TipoDeConta.valueOf("CONTA_CORRENTE").toModeltoBcb()
        ),
        donoConta = Owner(
            itauContaResponse = itauContaResponse
        )
    )
}

public fun bcbCriarPixResponseFake(): BcbCriarPixResponse {
    return BcbCriarPixResponse(
        keyType = "PHONE",
        key = "+5511964044444",
        bankAccount = BankAccountResponse(
            participant = "60701190",
            branch = "0001",
            accountNumber = "291900",
            accountType = "CACC"
        ),
        owner = OwnerResponse(
            type = "NATURAL_PERSON",
            name = "Rafael M C Ponte",
            taxIdNumber = "02467781054"
        ),
        createdAt = "2021-07-21T20:25:15.073291"
    )
}