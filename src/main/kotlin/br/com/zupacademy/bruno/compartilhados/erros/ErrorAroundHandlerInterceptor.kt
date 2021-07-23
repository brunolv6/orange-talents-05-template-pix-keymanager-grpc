package br.com.zupacademy.bruno.compartilhados.erros

import br.com.zupacademy.bruno.ErrorDetails
import com.google.protobuf.Any.*
import com.google.rpc.Code
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return context.proceed()
        } catch (ex: Exception) {
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when (ex) {

                is BadRequestErrorException -> io.grpc.Status.INTERNAL
                    .withCause(ex)
                    .withDescription(ex.message).asRuntimeException()

                is AlreadyExistsErrorException -> io.grpc.Status.ALREADY_EXISTS
                    .withCause(ex)
                    .withDescription(ex.message).asRuntimeException()

                is InconsistenciaTipoChaveEChaveException -> {
                    val statusProto: com.google.rpc.Status = com.google.rpc.Status.newBuilder()
                        .setCode(Code.INVALID_ARGUMENT.getNumber()) // com.google.rpc.Code
                        .setMessage(ex.message)
                        .addDetails(
                            com.google.protobuf.Any.pack(
                                ErrorDetails.newBuilder() // any do protobuf
                                    .setCode(401)
                                    .setMessage("Chave nao corresponde ao tipo de chave escolhido")
                                    .setCampo("Chave ou Tipo da Chave")
                                    .build()))
                        .build()

                    StatusProto.toStatusRuntimeException(statusProto)
                }

                is ConstraintViolationExceptionSpecial -> {
                    val errosDetails = ex.erros.map { e -> pack(
                        ErrorDetails.newBuilder() // any do protobuf
                            .setCode(401).setMessage(e.message).setCampo(e.propertyPath.toString())
                            .build())
                    }

                    val statusProto: com.google.rpc.Status = com.google.rpc.Status.newBuilder()
                        .setCode(Code.INVALID_ARGUMENT.getNumber()) // com.google.rpc.Code
                        .setMessage(ex.message)
                        .addAllDetails(errosDetails)
                        .build()

                    // nao deve ter return
                    StatusProto.toStatusRuntimeException(statusProto)
                }

                else -> io.grpc.Status.UNKNOWN
                    .withCause(ex)
                    .withDescription(ex.message).asRuntimeException()
            }

            responseObserver.onError(status)

        }
        return null
    }
}