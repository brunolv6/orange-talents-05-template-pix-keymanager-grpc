package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.ChavePixServiceRequest
import br.com.zupacademy.bruno.criarPix.enums.TipoDeChave
import br.com.zupacademy.bruno.criarPix.enums.TipoDeConta

fun ChavePixServiceRequest.toModel(tipoDeChave: TipoDeChave): RequestChavePixExterna{
    val tipoDeConta: TipoDeConta = TipoDeConta.valueOf(this.tipoConta.name)

    val chaveVerificada: String = tipoDeChave.seAleatorio(chave)

    return RequestChavePixExterna(this.idCliente, tipoDeChave, chaveVerificada, tipoDeConta)
}