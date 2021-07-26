package br.com.zupacademy.bruno.criarPix.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoDeContaTest{

    @Nested
    inner class CONTA_CORRENTE {

        @Test
        internal fun `se for CONTA_CORRENTE deve retornar CACC`() {
            with(TipoDeConta.CONTA_CORRENTE){
                assertEquals("CACC", toModeltoBcb())
            }
        }
    }

    @Nested
    inner class CONTA_POUPANCA {

        @Test
        internal fun `se for CONTA_POUPANCA deve retornar SVGS`() {
            with(TipoDeConta.CONTA_POUPANCA){
                assertEquals("SVGS", toModeltoBcb())
            }
        }
    }
}