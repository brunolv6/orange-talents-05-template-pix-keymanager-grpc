package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BcbDeleteRequestTest{

    @Test
    internal fun `deve retornar a instancia com os dados esperados`() {
        val instancia = BcbDeleteRequest("123", "60701190")

        with(instancia){
            assertEquals("123", key)
            assertEquals("60701190", participant)
        }
    }
}