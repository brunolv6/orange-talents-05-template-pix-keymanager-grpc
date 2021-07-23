package br.com.zupacademy.bruno.criarPix

import br.com.zupacademy.bruno.criarPix.ContaItau
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
