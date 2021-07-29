package br.com.zupacademy.bruno.consultarPix

import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.NotAuthorizedException
import br.com.zupacademy.bruno.compartilhados.erros.exceptions.NotFoundException
import br.com.zupacademy.bruno.compartilhados.validadores.isUUID
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

//@Introspected
sealed class RequestValidada {
    abstract fun encontrarDadosPix(repository: ChavePixRepository, bcbClient: BcbClient): DadosChavePixResponse

    @Introspected
    data class SistemaInterno(
        @field:NotBlank val pixId: String,
        @field:NotBlank @field:isUUID val clientId: String
    ): RequestValidada() {
        override fun encontrarDadosPix(repository: ChavePixRepository, bcbClient: BcbClient): DadosChavePixResponse {
            return repository.findByIdPix(pixId.toLong()).run {
                if(this.isEmpty) throw NotFoundException("Pix id=${pixId} nao encontrado")
                if(this.get().conta.idConta != clientId) throw NotAuthorizedException("Requisicao nao autorizada")
                this.get()
            }.let {DadosChavePixResponse.formadaPorChavePix(it)}
        }
    }

    @Introspected
    data class ValorChave(
        @field:NotBlank @field:Size(max = 77) val valorChave: String
    ): RequestValidada() {
        override fun encontrarDadosPix(repository: ChavePixRepository, bcbClient: BcbClient): DadosChavePixResponse {
            return repository.findByChave(valorChave).run{
                if(this.isPresent) DadosChavePixResponse.formadaPorChavePix(this.get())
                else bcbClient.buscarChavePix(valorChave).let {
                    when(it.status) {
                        HttpStatus.OK -> DadosChavePixResponse.formadaPorChavePix(it.body().toModel(""))
                        else -> throw NotFoundException("Chave pix ${valorChave} nao encontrada")
                    }
                }
            }
        }
    }

}
