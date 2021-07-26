package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix

import br.com.zupacademy.bruno.compartilhados.clientesExternos.*
import br.com.zupacademy.bruno.utils.bcbCriarPixRequestFake
import br.com.zupacademy.bruno.utils.itauContaResponseFake
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BcbCriarPixRequestTest{


    @Test
    internal fun `objetos BcbCriarPixRequest quando se tratar do mesmo devem ser gerar mesmo hashcode e ser iguais na comparacao`() {

        val itauContaResponse = itauContaResponseFake()

        val bcbCriarPixRequest = bcbCriarPixRequestFake(itauContaResponse)

        val itauContaResponse2 = itauContaResponseFake()

        val bcbCriarPixRequest2 = bcbCriarPixRequestFake(itauContaResponse2)

        with(bcbCriarPixRequest){
            assertTrue(bcbCriarPixRequest.hashCode() == bcbCriarPixRequest2.hashCode())
            assertTrue(bcbCriarPixRequest.equals(bcbCriarPixRequest2))
        }
    }

}