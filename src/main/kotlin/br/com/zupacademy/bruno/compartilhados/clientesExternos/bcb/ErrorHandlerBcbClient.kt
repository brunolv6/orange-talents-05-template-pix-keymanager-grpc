package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb

import io.micronaut.aop.Around

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Around
annotation class ErrorHandlerBcbClient
