package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.ChavePixServiceGrpc
import br.com.zupacademy.bruno.ChavePixServiceRequest
import br.com.zupacademy.bruno.TipoChave
import br.com.zupacademy.bruno.TipoConta
import br.com.zupacademy.bruno.compartilhados.clientesExternos.*
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.BcbClient
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.BankAccountResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.BcbCriarPixResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.criarPix.OwnerResponse
import br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.ERPItauClient
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import br.com.zupacademy.bruno.utils.bcbCriarPixRequestFake
import br.com.zupacademy.bruno.utils.bcbCriarPixResponseFake
import br.com.zupacademy.bruno.utils.itauContaResponseFake
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status

import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(
    rollback = false,
    transactionMode = TransactionMode.SINGLE_TRANSACTION, // trend unica do começo ao final do teste
    transactional = false
)
internal class CriarChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: ChavePixServiceGrpc.ChavePixServiceBlockingStub
){

    @field:Inject
    lateinit var erpItauClient: ERPItauClient

    @field:Inject
    lateinit var bcbClient: BcbClient

    @field:Inject
    @field:Client("/")
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
    internal fun `registra e persiste chave pix`() {

        val request = requestFake("c56dfef4-7901-44fb-84e2-a2cefb157890")

        val itauContaResponse = itauContaResponseFake()

        Mockito.`when`(erpItauClient.consultarConta(request.idCliente, request.tipoConta.name)).thenReturn(HttpResponse.ok(itauContaResponse))

        val bcbCriarPixRequest = bcbCriarPixRequestFake(itauContaResponse)

        val bcbCriarPixResponse = bcbCriarPixResponseFake()

        Mockito.`when`(bcbClient.criarChavePix(bcbCriarPixRequest)).thenReturn(HttpResponse.created(bcbCriarPixResponse))

        val response = grpcClient.cadastrar(request)

        // validacoes
        with(response){
            Assertions.assertEquals(1, repository.count())
            Assertions.assertTrue(repository.existsByChave("+5511964044444"))
            Assertions.assertNotNull(pixId)
        }

    }

    @Test
    internal fun `nao deve cadastrar pix por inconsistencia entre chave e tipo de Chave`() {
        // cenario

        // acao
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(ChavePixServiceRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.CPF)
                .setChave("+5511964047742")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        // validacoes
        with(error){
            Assertions.assertEquals(0, repository.count())
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Apresentou erros de validacao", status.description)
        }
    }

    @Test
    internal fun `nao deve cadastrar chave por ter dados invalidos no request`() {
        // cenario

        // acao
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(ChavePixServiceRequest.newBuilder()
                .setIdCliente("")
                .setTipoChave(TipoChave.ALEATORIO)
                .setChave("")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        // validacoes
        with(error){
            Assertions.assertEquals(0, repository.count())
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Apresentou erros de validacao genericos", status.description)
        }
    }

    @Test
    internal fun `nao deve cadastrar o pix pois conta nao existe no itau`() {
        val request = requestFake("c56dfef4-7901-44fb-84e2-a2cefb157893")

        Mockito.`when`(erpItauClient.consultarConta(request.idCliente, request.tipoConta.name)).thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(request)
        }

        // Validação
        with(error) {
            assertEquals(Status.INTERNAL.code, status.code)
            assertEquals("INTERNAL: Conta não encontrada!", message)
            assertEquals(0, repository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar o pix pois conta ja existe no bcb`() {
        // contexto

        val request = requestFake("c56dfef4-7901-44fb-84e2-a2cefb157890")

        val itauContaResponse = itauContaResponseFake()

        Mockito.`when`(erpItauClient.consultarConta(request.idCliente, request.tipoConta.name)).thenReturn(HttpResponse.ok(itauContaResponse))

        val bcbCriarPixRequest = bcbCriarPixRequestFake(itauContaResponse)

        Mockito.`when`(bcbClient.criarChavePix(bcbCriarPixRequest)).thenReturn(HttpResponse.unprocessableEntity())

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(request)
        }

        // Validação
        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("ALREADY_EXISTS: Chave Pix já existe no BCB", message)
            assertEquals(0, repository.count())
        }

    }


    @MockBean(ERPItauClient::class)
    fun itauClient(): ERPItauClient? {
        return Mockito.mock(ERPItauClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun bcbPixClient(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }

    private fun requestFake(idClient: String?): ChavePixServiceRequest{
        return ChavePixServiceRequest.newBuilder()
            .setIdCliente(idClient)
            .setTipoChave(TipoChave.CELULAR)
            .setChave("+5511964044444")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
    }

}

@Factory
class Clients {

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ChavePixServiceGrpc.ChavePixServiceBlockingStub? {
        return ChavePixServiceGrpc.newBlockingStub(channel)
    }
}
