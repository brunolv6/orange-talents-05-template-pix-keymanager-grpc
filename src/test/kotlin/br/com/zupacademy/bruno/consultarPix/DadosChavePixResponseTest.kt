package br.com.zupacademy.bruno.consultarPix

import br.com.zupacademy.bruno.TipoConta
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.entidades.ContaItau
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta
import br.com.zupacademy.bruno.utils.addChavePixRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class DadosChavePixResponseTest{

    @Test
    internal fun `deve criar dados a partir de uma chave pix de forma correta`() {
        val chavePixInicial = ChavePix(
            tipoDaChave = "CPF",
            chave = "24876431701",
            conta = ContaItau(
                nome = "Cassio Almeida",
                cpf = "31643468081",
                instituicaoId = "60701190",
                agencia = "0001",
                numeroConta = "084329",
                idConta = "de95a228-1f27-4ad2-907e-e5a2d816e9bc"
            ),
            tipoDaConta = "CACC"
        )

        val response = DadosChavePixResponse.formadaPorChavePix(chavePixInicial)

        with(response){
            assertEquals(chavePixInicial.tipoDaChave, tipoChave.name)
            assertEquals(chavePixInicial.chave, valorChave)
            assertEquals(chavePixInicial.conta.nome, conta.nome)
            assertEquals(null, pixId)
            assertEquals(chavePixInicial.conta.idConta, clienteId)
            assertEquals("CONTA_CORRENTE", tipoConta.toString())
        }
    }
}