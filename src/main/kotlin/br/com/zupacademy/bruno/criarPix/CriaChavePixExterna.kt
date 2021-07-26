package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.ERPItauClient
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.AlreadyExistsErrorException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.BadRequestErrorException
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CriaChavePixExterna(
    val erpItauClient: ERPItauClient,
    val bcbClient: BcbClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun registrar(request: RequestChavePixExterna): ChavePix {

        val contaItauResponse = erpItauClient.consultarConta(request.idCliente, request.tipoConta.name).body() ?: throw BadRequestErrorException("Conta não encontrada!")

        logger.info("Conta recebida do ERP Itau")

        val bcbCriarPixRequest = contaItauResponse.toModel(request)

        val chavePixBcbReponse = bcbClient.criarChavePix(bcbCriarPixRequest).body() ?: throw AlreadyExistsErrorException("Chave Pix já existe no BCB")

        logger.info("Chave PIX salva com sucesso no BCB")

        return chavePixBcbReponse.toModel(request.idCliente)

    }

}
