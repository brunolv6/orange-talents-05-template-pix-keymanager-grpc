package br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.consultarConta

import br.com.zupacademy.bruno.compartilhados.clientesExternos.Instituicao
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class InstituicaoTest{

    @Test
    internal fun `deve ser formado como esperado a instituicao`() {
        val instituicao = Instituicao(
            nome = "Bruno",
            ispb = "123"
        )

        with(instituicao){
            assertEquals("Bruno", instituicao.nome)
            assertEquals("123", instituicao.ispb)
        }
    }
}