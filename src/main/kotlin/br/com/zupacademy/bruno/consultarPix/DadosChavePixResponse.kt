package br.com.zupacademy.bruno.consultarPix

import br.com.zupacademy.bruno.ConsultarChavePixReponse
import br.com.zupacademy.bruno.TipoChave
import br.com.zupacademy.bruno.TipoConta
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.entidades.ContaItau
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

class DadosChavePixResponse (
    val pixId: Long? = null, // pode ou não ser preenchido dependendo do tipo de consulta
    val clienteId: String? = null,
    val tipoChave: TipoChave,
    val valorChave: String,
    val tipoConta: TipoConta,
    val conta: ContaItau,
    val dataCadastro: LocalDateTime
){
    fun toModelGrpc(): ConsultarChavePixReponse? {
        val criadoAs = dataCadastro.atZone(ZoneId.of("UTC")).toInstant()
        val criadoAsGrpc = Timestamp.newBuilder()
                                .setSeconds(criadoAs.epochSecond)
                                .setNanos(criadoAs.nano)
                                .build()

        return ConsultarChavePixReponse.newBuilder()
            .setPixId(pixId?.toString() ?: "")
            .setClienteId(clienteId?.toString() ?: "")
            .setTipoChave(tipoChave)
            .setChave(valorChave)
            .setNomeTitular(conta.nome)
            .setCpf(conta.cpf)
            .setConta(ConsultarChavePixReponse.Conta.newBuilder()
                .setNomeInstituicao(conta.instituicaoId)
                .setAgencia(conta.agencia)
                .setNumeroConta(conta.numeroConta)
                .setTipoContaValue(1)
                .build())
            .setDataCriacao(criadoAsGrpc)
            .build()
    }

    companion object {
        fun formadaPorChavePix(chavePix: ChavePix): DadosChavePixResponse {
            val tipoConta = when(chavePix.tipoDaConta){
                "CACC" -> "CONTA_CORRENTE"
                else -> "CONTA_POUPANCA"
            }

            return DadosChavePixResponse(
                pixId = chavePix.idPix,
                clienteId = chavePix.conta.idConta,
                tipoChave = TipoChave.valueOf(chavePix.tipoDaChave),
                valorChave = chavePix.chave,
                tipoConta = TipoConta.valueOf(tipoConta),
                conta = chavePix.conta,
                dataCadastro = chavePix.criadaEm
            )
        }
    }
}