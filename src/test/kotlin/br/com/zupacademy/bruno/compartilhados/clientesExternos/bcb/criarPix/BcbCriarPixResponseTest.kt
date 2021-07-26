package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix

import br.com.zupacademy.bruno.utils.bcbCriarPixRequestFake
import br.com.zupacademy.bruno.utils.bcbCriarPixResponseFake
import br.com.zupacademy.bruno.utils.itauContaResponseFake
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BcbCriarPixResponseTest{

    @Test
    internal fun `objetos BcbCriarPixResponse quando se tratar do mesmo devem ser gerar mesmo hashcode e ser iguais na comparacao`() {

        val bcbCriarPixResponse = bcbCriarPixResponseFake()

        val bcbCriarPixResponse2 = bcbCriarPixResponseFake()

        with(bcbCriarPixResponse){
            assertTrue(bcbCriarPixResponse.hashCode() == bcbCriarPixResponse2.hashCode())
            assertTrue(bcbCriarPixResponse.equals(bcbCriarPixResponse2))
        }
    }

    @Test
    internal fun `testando construtor do  BcbCriarPixResponse`() {

        val contaBanco = BankAccountResponse(
            participant = "60701190",
            branch = "0001",
            accountNumber = "291900",
            accountType = "CACC"
        )

        val titular = OwnerResponse(
            type = "NATURAL_PERSON",
            name = "Rafael M C Ponte",
            taxIdNumber = "02467781054"
        )

        val bcbCriarPixResponse = BcbCriarPixResponse(
            keyType = "PHONE",
            key = "+5511964044444",
            bankAccount = contaBanco,
            owner = titular,
            createdAt = "2021-07-21T20:25:15.073291"
        )


        with(bcbCriarPixResponse){
            assertEquals("PHONE", keyType)
            assertEquals("+5511964044444", key)
            assertEquals("60701190", contaBanco.participant)
            assertEquals("0001", contaBanco.branch)
            assertEquals("291900", contaBanco.accountNumber)
            assertEquals("CACC", contaBanco.accountType)
            assertEquals("NATURAL_PERSON", titular.type)
            assertEquals("Rafael M C Ponte", titular.name)
            assertEquals("02467781054", titular.taxIdNumber)
            assertEquals("2021-07-21T20:25:15.073291", createdAt)
        }
    }
}