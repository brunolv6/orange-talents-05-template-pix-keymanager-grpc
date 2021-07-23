package br.com.zupacademy.bruno.criarPix.entidades

import br.com.zupacademy.bruno.criarPix.entidades.ContaItau
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class ChavePix(
    val tipoDaChave: String,
    val chave: String,
    val conta: ContaItau,
    val tipoDaConta: String
) {

    @Id
    @GeneratedValue
    val idPix: Long? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}
