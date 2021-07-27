package br.com.zupacademy.bruno.deletarPix

import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbDeleteRequest
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.BadRequestErrorException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.NotAuthorizedException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.NotFoundException
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import com.google.api.Http
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class GerenciarRemocaoChavePix(
    val chavePixRepository: ChavePixRepository,
    val bcbClient: BcbClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun processa(@Valid request: RequestDeletarChave) {

        // garantir existencia e validar conta
        val chavePix = chavePixRepository.findByIdPix(request.pixId).run {
            if(this.isEmpty) throw NotFoundException("Nao encontrado registro do pixId ${request.pixId}")
            this.ifPresent { if(it.conta.idConta != request.clienteId) throw NotAuthorizedException("Cliente de id ${request.clienteId} nao autorizado") }
            this.get()
        }
        logger.info("Chave de id ${request.pixId} existe e cliente tem autorizacao")

        // buscar chave no bcb
        val bcbBuscaResponse = bcbClient.buscarChavePix(chavePix.chave)
        if (bcbBuscaResponse.status != HttpStatus.OK) throw NotFoundException("Chave ${chavePix.chave} nao encontrada no BCB")
        logger.info("Chave ${chavePix.chave} encontrada no sistema externo BCB")

        // se encontrada -> Deletar chav pix do BCB
        val bcbDeleteRequest = BcbDeleteRequest(chavePix.chave)
        val bcbDeletaResponse = bcbClient.deletarChavePix(chavePix.chave, bcbDeleteRequest)

        when(bcbBuscaResponse.status) {
            HttpStatus.OK,
            HttpStatus.NOT_FOUND -> logger.info("Chave Pix ${chavePix.chave} de id ${request.pixId} foi deletada no sistema externo BCB")

            HttpStatus.UNAUTHORIZED -> throw NotAuthorizedException("Nao autorizado exclusão em sitema externo")

            else -> throw BadRequestErrorException("Erro inesperado em sistema externo do BCB")
        }

        chavePixRepository.delete(chavePix)
        logger.info("Chave Pix ${chavePix.chave} de id ${request.pixId} foi deletada no sistema interno")
    }
}
