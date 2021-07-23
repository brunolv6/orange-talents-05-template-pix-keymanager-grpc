package br.com.zupacademy.bruno.compartilhados.validadores

import br.com.zupacademy.bruno.ChavePixServiceRequest
import br.com.zupacademy.bruno.ErrorDetails
import br.com.zupacademy.bruno.criarPix.TipoDeChave
import com.google.rpc.Code
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton


@Singleton
@InterceptorBean(ChavePixValida::class)
class ChavePixValidaInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        val request = context.parameterValues[0] as ChavePixServiceRequest
        val responseObserver = context.parameterValues[1] as StreamObserver<*>

        val tipoDeChave: TipoDeChave = TipoDeChave.valueOf(request.tipoChave.name)

        if(!tipoDeChave.valida(request.chave)) {
            val statusProto: com.google.rpc.Status = com.google.rpc.Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT.getNumber()) // com.google.rpc.Code
                .setMessage("Apresentou erros de validacao")
                .addDetails(
                    com.google.protobuf.Any.pack(
                    ErrorDetails.newBuilder() // any do protobuf
                        .setCode(401)
                        .setMessage("Chave nao corresponde ao tipo de chave escolhido")
                        .setCampo("Chave ou Tipo da Chave")
                        .build()))
                .build()

            responseObserver?.onError(StatusProto.toStatusRuntimeException(statusProto))

            return null // important retornar null porque o contexto foi finalizado
        }

        return context.proceed()
    }
}