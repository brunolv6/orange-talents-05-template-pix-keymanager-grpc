package br.com.zupacademy.bruno.deletarPix

import br.com.zupacademy.bruno.ChavePixServiceGrpc
import br.com.zupacademy.bruno.DeletarChavePixRequest
import br.com.zupacademy.bruno.DeletarChavePixServiceGrpc
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbDeleteRequest
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.entidades.ContaItau
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Singleton
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject

// teste de  registro e exclusao do PIX no BCB já implementei na tarefa numeor 006 e 011, porque achei que era necessaria baseado na arquitura do projeto e requisitos
@MicronautTest(
    rollback = false,
    transactionMode = TransactionMode.SINGLE_TRANSACTION,
    transactional = false
)
internal class DeletarChavePixEndpointTest(
    val grpc: DeletarChavePixServiceGrpc.DeletarChavePixServiceBlockingStub,
    val repository: ChavePixRepository
){

    @field:Inject
    lateinit var bcbClient: BcbClient

    @field:Inject
    @field:io.micronaut.http.client.annotation.Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    fun setUp(){
        repository.deleteAll()
    }

    @AfterEach
    fun getDown(){
        repository.deleteAll()
    }

    @Test
    internal fun `nao deve deletar chave por ter dados invalidos no request`() {
        // acao
        val responde = assertThrows<StatusRuntimeException> {
            grpc.excluir(DeletarChavePixRequest.newBuilder()
                .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb1578")
                .setPixId("1")
                .build())
        }

        // validacoes
        with(responde){
            Assertions.assertEquals(0, repository.count())
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Apresentou erros de validacao genericos", status.description)
        }
    }

    @Test
    internal fun `nao deve deletar chave por nao encontrar pixId`() {
        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setPixId("1")
            .build()

        // acao
        val responde = assertThrows<StatusRuntimeException> {
            grpc.excluir(request)
        }

        // validacoes
        with(responde){
            Assertions.assertEquals(0, repository.count())
            Assertions.assertEquals(Status.NOT_FOUND.code, status.code)
            Assertions.assertEquals("Nao encontrado registro do pixId ${request.pixId}", status.description)
        }
    }

    @Test
    internal fun `nao deve deletar chave por nao ter autorizacao`() {

        // contexto
        val chavePixExistente = addChavePixRepository()

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        // acao
        val responde = assertThrows<StatusRuntimeException> {
            grpc.excluir(request)
        }

        // validacoes
        with(responde){
            Assertions.assertEquals(1, repository.count())
            Assertions.assertEquals(Status.PERMISSION_DENIED.code, status.code)
            Assertions.assertEquals("Cliente de id ${request.clienteId} nao autorizado", status.description)
        }
    }

    @Test
    internal fun `nao deve deletar a chave pix por ocorrer um erro inesperado no sistema externo do bcb no momento de buscar`() {
        // contexto
        val chavePixExistente = addChavePixRepository()

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId(chavePixExistente.conta.idConta)
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        Mockito.`when`(bcbClient.buscarChavePix(chavePixExistente.chave)).thenReturn(HttpResponse.serverError())

        // acao
        val error = assertThrows<StatusRuntimeException> {
            grpc.excluir(request)
        }

        // validacoes
        with(error) {
            assertEquals(Status.INTERNAL.code, status.code)
            assertEquals("INTERNAL: Erro inesperado em sistema externo do BCB no momento de buscar a chave pix", message)
            assertEquals(1, repository.count())
        }
    }

    @Test
    internal fun `deve deletar a chave pix quando nao a encontrar no bcb`() {
        // contexto
        val chavePixExistente = addChavePixRepository()

        assertEquals(1, repository.count())

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId(chavePixExistente.conta.idConta)
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        Mockito.`when`(bcbClient.buscarChavePix(chavePixExistente.chave)).thenReturn(HttpResponse.notFound())

        // acao
        val response = grpc.excluir(request)

        // validacoes
        with(response) {
            assertEquals(0, repository.count())
            assertEquals(true, removido)
        }
    }

    @Test
    internal fun `nao deve deletar a chave pix por ocorrer um erro inesperado no sistema externo do bcb no momento de deletar`() {
        // contexto
        val chavePixExistente = addChavePixRepository()

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId(chavePixExistente.conta.idConta)
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        Mockito.`when`(bcbClient.buscarChavePix(chavePixExistente.chave)).thenReturn(HttpResponse.ok())

        val bcbDeleteRequest = BcbDeleteRequest(
            key = chavePixExistente.chave
        )

        Mockito.`when`(bcbClient.deletarChavePix(chavePixExistente.chave, bcbDeleteRequest)).thenReturn(HttpResponse.badRequest())

        // acao
        val error = assertThrows<StatusRuntimeException> {
            grpc.excluir(request)
        }

        // validacoes
        with(error) {
            assertEquals(Status.INTERNAL.code, status.code)
            assertEquals("INTERNAL: Erro inesperado em sistema externo do BCB no momento de deletar a chave pix", message)
            assertEquals(1, repository.count())
        }
    }

    @Test
    internal fun `nao deve deletar a chave pix por falta de autorizacao da instituicao no sistema externo do bcb no momento de deletar`() {
        // contexto
        val chavePixExistente = addChavePixRepository()

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId(chavePixExistente.conta.idConta)
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        Mockito.`when`(bcbClient.buscarChavePix(chavePixExistente.chave)).thenReturn(HttpResponse.ok())

        val bcbDeleteRequest = BcbDeleteRequest(
            key = chavePixExistente.chave
        )

        Mockito.`when`(bcbClient.deletarChavePix(chavePixExistente.chave, bcbDeleteRequest)).thenReturn(HttpResponse.unauthorized())

        // acao
        val error = assertThrows<StatusRuntimeException> {
            grpc.excluir(request)
        }

        // validacoes
        with(error) {
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            assertEquals("PERMISSION_DENIED: Nao autorizado exclusão em sitema externo", message)
            assertEquals(1, repository.count())
        }
    }
    @Test
    internal fun `deve deletar a chave pix quando nao a encontrar no bcb no momento de deletar`() {
        // contexto
        val chavePixExistente = addChavePixRepository()

        assertEquals(1, repository.count())

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId(chavePixExistente.conta.idConta)
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        Mockito.`when`(bcbClient.buscarChavePix(chavePixExistente.chave)).thenReturn(HttpResponse.ok())

        val bcbDeleteRequest = BcbDeleteRequest(
            key = chavePixExistente.chave
        )

        Mockito.`when`(bcbClient.deletarChavePix(chavePixExistente.chave, bcbDeleteRequest)).thenReturn(HttpResponse.notFound())

        // acao
        val response = grpc.excluir(request)

        // validacoes
        with(response) {
            assertEquals(0, repository.count())
            assertEquals(true, removido)
        }
    }

    @Test
    internal fun `deve deletar a chave pix quando a deleetar no sistema externo BCB`() {
        // contexto
        val chavePixExistente = addChavePixRepository()

        assertEquals(1, repository.count())

        val request = DeletarChavePixRequest.newBuilder()
            .setClienteId(chavePixExistente.conta.idConta)
            .setPixId(chavePixExistente.idPix.toString())
            .build()

        Mockito.`when`(bcbClient.buscarChavePix(chavePixExistente.chave)).thenReturn(HttpResponse.ok())

        val bcbDeleteRequest = BcbDeleteRequest(
            key = chavePixExistente.chave
        )

        Mockito.`when`(bcbClient.deletarChavePix(chavePixExistente.chave, bcbDeleteRequest)).thenReturn(HttpResponse.ok())

        // acao
        val response = grpc.excluir(request)

        // validacoes
        with(response) {
            assertEquals(0, repository.count())
            assertEquals(true, removido)
        }
    }


    fun addChavePixRepository(): ChavePix {
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
            tipoDaConta = "CONTA_CORRENTE"
        )

        repository.save(chavePixInicial)

        return chavePixInicial
    }

    @MockBean(BcbClient::class)
    fun bcbPixClient(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Client {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): DeletarChavePixServiceGrpc.DeletarChavePixServiceBlockingStub?{
            return DeletarChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

}