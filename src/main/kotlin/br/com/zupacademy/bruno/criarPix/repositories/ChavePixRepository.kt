package br.com.zupacademy.bruno.criarPix.repositories

import br.com.zupacademy.bruno.criarPix.entidades.ChavePix
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, Long> {

//    @Query("SELECT Count(*) FROM Chave_Pix cp WHERE cp.chave = :chave AND cp.id_conta = :idconta", nativeQuery = true)
//    fun existeChaveEIdConta(chave: String, idconta: String): Int

    fun existsByChave(chave: String?): Boolean

    fun findByIdPix(pixId: Long?): Optional<ChavePix>
}