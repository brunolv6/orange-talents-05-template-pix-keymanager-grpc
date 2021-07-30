package br.com.zupacademy.bruno.listarPix

import br.com.zupacademy.bruno.ListarChavePixRequest
import br.com.zupacademy.bruno.ListarChavePixServiceGrpc
import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import br.com.zupacademy.bruno.criarPix.entidades.ContaItau
import br.com.zupacademy.bruno.criarPix.repositories.ChavePixRepository
import br.com.zupacademy.bruno.utils.addChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Singleton
import org.junit.jupiter.api.assertThrows

@MicronautTest(
    transactionMode = TransactionMode.SINGLE_TRANSACTION,
    transactional = false,
    rollback = false
)
internal class ListarChavePixTest(
    val repository: ChavePixRepository,
    val grpcLista: ListarChavePixServiceGrpc.ListarChavePixServiceBlockingStub
){

    @BeforeEach
    fun setUp(){
        repository.deleteAll()
    }

    @AfterEach
    fun getDown(){
        repository.deleteAll()
    }

    @Test
    internal fun `nao deve listar chaves pix se o id do cliente for invalido`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcLista.listar(ListarChavePixRequest.newBuilder()
                .setClientId("")
                .build())
        }

        with(error){
            assertEquals("INVALID_ARGUMENT: Apresentou erros de validacao genericos", message)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `deve apresentar lista vazia se cliente id nao tiver nenhuma cadastrada`() {

        val response = grpcLista.listar(ListarChavePixRequest.newBuilder()
                .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .build())

        with(response){
            assertEquals("c56dfef4-7901-44fb-84e2-a2cefb157890", clienteId)
            assertTrue(chavePixList.isEmpty())
        }
    }

    @Test
    internal fun `deve apresentar lista de chaves do cliente id`() {
        // contexto
        val chavePixExistente = addChavePixRepository(repository)
        val chavePixExistente_2 = ChavePix(
            tipoDaChave = "CPF",
            chave = "24876431702",
            conta = ContaItau(
                nome = "Cassio Almeida",
                cpf = "31643468081",
                instituicaoId = "60701190",
                agencia = "0001",
                numeroConta = "084329",
                idConta = chavePixExistente.conta.idConta
            ),
            tipoDaConta = "CONTA_CORRENTE"
        )

        repository.save(chavePixExistente_2)

        val chavePixExistente_3 = ChavePix(
            tipoDaChave = "CPF",
            chave = "24876431700",
            conta = ContaItau(
                nome = "Cassio Almeida",
                cpf = "31643468081",
                instituicaoId = "60701190",
                agencia = "0001",
                numeroConta = "084329",
                idConta = "de95a228-1f27-4ad2-907e-e5a2d816e9b7"
            ),
            tipoDaConta = "CONTA_CORRENTE"
        )

        repository.save(chavePixExistente_3)

        val response = grpcLista.listar(ListarChavePixRequest.newBuilder()
            .setClientId("de95a228-1f27-4ad2-907e-e5a2d816e9bc")
            .build())

        with(response){
            assertEquals("de95a228-1f27-4ad2-907e-e5a2d816e9bc", clienteId)
            assertTrue(chavePixList.size == 2)
        }
    }

    @Factory
    class Client{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ListarChavePixServiceGrpc.ListarChavePixServiceBlockingStub?{
            return ListarChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}
