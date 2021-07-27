package br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.consultarConta

import br.com.zupacademy.bruno.compartilhados.clientesExternos.Instituicao
import br.com.zupacademy.bruno.compartilhados.clientesExternos.ItauContaResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.Titular
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ItauContaResponseTest{

    @Test
    internal fun `deve ser formado como esperado a itau`() {

        val titular = Titular(
            id = "1",
            nome = "Bruno",
            cpf = "11234567890"
        )

        val instituicao = Instituicao(
            nome = "Itau",
            ispb = "123"
        )

        val itauContaResponse = ItauContaResponse(
            tipo = "CONTA",
            instituicao = instituicao,
            agencia = "1111",
            numero = "2222",
            titular = titular
        )

        with(instituicao){
            assertEquals("Itau", instituicao.nome)
            assertEquals("123", instituicao.ispb)
            assertEquals("1", titular.id)
            assertEquals("CONTA", itauContaResponse.tipo)
        }
    }
}