package br.com.zupacademy.bruno.consultarPix

import br.com.zupacademy.bruno.ConsultarChavePixRequest
import br.com.zupacademy.bruno.ConsultarChavePixServiceGrpc
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import br.com.zupacademy.bruno.utils.addChavePixRepository
import br.com.zupacademy.bruno.utils.bcbCriarPixResponseFake
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(
    transactional = false,
    transactionMode = TransactionMode.SINGLE_TRANSACTION,
    rollback = false
)
internal class ConsultarChavePixEndpointTest(
    val grpcConsulta: ConsultarChavePixServiceGrpc.ConsultarChavePixServiceBlockingStub,
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
    internal fun `nao deve consultar porque os dados de entrada do tipo SISTEMA INTERNO sao invalidos`() {
        val error = assertThrows<StatusRuntimeException> {
                grpcConsulta.consultar(
                    ConsultarChavePixRequest.newBuilder().setSistemaInterno(
                        ConsultarChavePixRequest.SistemaInterno.newBuilder()
                            .setPixId("")
                            .setClientId("")
                    ).build()
                )
            }

        with(error){
            assertEquals(0, repository.count())
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Dados de entrada invalidos", status.description)
        }
    }

    @Test
    internal fun `nao deve consultar porque os dados de entrada do tipo VALORPIX sao invalidos`() {
        val error = assertThrows<StatusRuntimeException> {
                grpcConsulta.consultar(
                    ConsultarChavePixRequest.newBuilder()
                        .setValorChave("")
                        .build()
                )
            }

        with(error){
            assertEquals(0, repository.count())
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Dados de entrada invalidos", status.description)
        }
    }

    @Test
    internal fun `nao deve consultar porque o pix id não existe`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcConsulta.consultar(
                ConsultarChavePixRequest.newBuilder().setSistemaInterno(
                    ConsultarChavePixRequest.SistemaInterno.newBuilder()
                        .setPixId("2")
                        .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                ).build()
            )
        }

        with(error){
            assertEquals(0, repository.count())
            assertEquals(Status.NOT_FOUND.code, status.code)
            Assertions.assertEquals("Pix id=2 nao encontrado", status.description)
        }
    }


    @Test
    internal fun `nao deve consultar porque o cliente nao tem autorizacao para aquele pix id`() {

        // contexto
        val chavePixExistente = addChavePixRepository(repository)

        val error = assertThrows<StatusRuntimeException> {
            grpcConsulta.consultar(
                ConsultarChavePixRequest.newBuilder().setSistemaInterno(
                    ConsultarChavePixRequest.SistemaInterno.newBuilder()
                        .setPixId(chavePixExistente.idPix.toString())
                        .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                ).build()
            )
        }

        with(error){
            assertEquals(1, repository.count())
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            Assertions.assertEquals("Requisicao nao autorizada", status.description)
        }
    }


    @Test
    internal fun `deve consultar quando pix id e client id estiverem corretos e existirem`() {

        // contexto
        val chavePixExistente = addChavePixRepository(repository)

        val response = grpcConsulta.consultar(
                ConsultarChavePixRequest.newBuilder().setSistemaInterno(
                    ConsultarChavePixRequest.SistemaInterno.newBuilder()
                        .setPixId(chavePixExistente.idPix.toString())
                        .setClientId(chavePixExistente.conta.idConta)
                ).build()
            )

        with(response){
            assertEquals(1, repository.count())
            assertEquals(chavePixExistente.idPix.toString(), pixId)
            assertEquals(chavePixExistente.conta.idConta, clienteId)
        }
    }

    @Test
    internal fun `deve consultar quando valor pix estiver correto e existir no sistema interno`() {

        // contexto
        val chavePixExistente = addChavePixRepository(repository)

        val response = grpcConsulta.consultar(
                ConsultarChavePixRequest.newBuilder()
                    .setValorChave(chavePixExistente.chave)
                    .build()
            )

        with(response){
            assertEquals(1, repository.count())
            assertEquals(chavePixExistente.idPix.toString(), pixId)
            assertEquals(chavePixExistente.conta.idConta, clienteId)
            assertEquals(chavePixExistente.chave, chave)
        }
    }

    @Test
    internal fun `deve consultar quando valor pix estiver correto e existir`() {

        // contexto
        val chavePixExistente = addChavePixRepository(repository)

        val bcbCriarPixResponse = bcbCriarPixResponseFake()

        Mockito.`when`(bcbClient.buscarChavePix("02467781054")).thenReturn(HttpResponse.ok(bcbCriarPixResponse))

        val response = grpcConsulta.consultar(
            ConsultarChavePixRequest.newBuilder()
                .setValorChave("02467781054")
                .build()
        )

        with(response){
            assertEquals(1, repository.count())
            assertEquals(bcbCriarPixResponse.key, chave)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbPixClient(): BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Client{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ConsultarChavePixServiceGrpc.ConsultarChavePixServiceBlockingStub?{
            return ConsultarChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}