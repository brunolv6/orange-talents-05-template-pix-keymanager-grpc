package br.com.zupacademy.bruno.criarPix.enums

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import java.util.*

internal class TipoDeChaveTest {

    @Nested
    inner class ALEATORIO {

        @Test
        internal fun `deve ser valida quando chave for vazia`() {
            with(TipoDeChave.ALEATORIO){
                assertTrue(valida(""))
            }
        }

        @Test
        internal fun `nao deve ser valida se já tiver valor definido`() {
            with(TipoDeChave.ALEATORIO){
                assertFalse(valida(UUID.randomUUID().toString()))
            }
        }

        @Test
        internal fun `deve retornar RAMDOM para modelagem dos dados para o cliente bcb`() {
            with(TipoDeChave.ALEATORIO){
                assertEquals("RANDOM", toModeltoBcb())
            }
        }

        @Test
        internal fun `deve retornar a propria chave no caso de CPF`() {
            with(TipoDeChave.ALEATORIO){
                assertTrue(seAleatorio("").matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}".toRegex()))
            }
        }
    }

    @Nested
    inner class CPF {

        @Test
        internal fun `deve ser valida quando chave for um cpf`() {
            with(TipoDeChave.CPF){
                assertTrue(valida("60211518875"))
            }
        }

        @Test
        internal fun `nao deve ser valida se a chave nao for um cpf`() {
            with(TipoDeChave.CPF){
                assertFalse(valida("nao e cpf"))
            }
        }

        @Test
        internal fun `deve retornar CPF para modelagem dos dados para o cliente bcb`() {
            with(TipoDeChave.CPF){
                assertEquals("CPF", toModeltoBcb())
            }
        }

        @Test
        internal fun `deve retornar a propria chave no caso de CPF`() {
            with(TipoDeChave.CPF){
                assertEquals("60211518875", seAleatorio("60211518875"))
            }
        }
    }

    @Nested
    inner class CELULAR {

        @Test
        internal fun `deve ser valida quando chave for um celular`() {
            with(TipoDeChave.CELULAR){
                assertTrue(valida("+5511987654321"))
            }
        }

        @Test
        internal fun `nao deve ser valida se a chave nao for um celular`() {
            with(TipoDeChave.CELULAR){
                assertFalse(valida("nao e celular"))
            }
        }

        @Test
        internal fun `deve retornar PHONE para modelagem dos dados para o cliente bcb`() {
            with(TipoDeChave.CELULAR){
                assertEquals("PHONE", toModeltoBcb())
            }
        }

        @Test
        internal fun `deve retornar a propria chave no caso de CELULAR`() {
            with(TipoDeChave.CELULAR){
                assertEquals("+5511987654321", seAleatorio("+5511987654321"))
            }
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        internal fun `deve ser valida quando chave for um email`() {
            with(TipoDeChave.EMAIL){
                assertTrue(valida("teste@gmail.com"))
            }
        }

        @Test
        internal fun `nao deve ser valida se a chave nao for um email`() {
            with(TipoDeChave.EMAIL){
                assertFalse(valida("testegmail.com"))
            }
        }

        @Test
        internal fun `deve retornar PHONE para modelagem dos dados para o cliente bcb`() {
            with(TipoDeChave.EMAIL){
                assertEquals("EMAIL", toModeltoBcb())
            }
        }

        @Test
        internal fun `deve retornar a propria chave no caso de CELULAR`() {
            with(TipoDeChave.EMAIL){
                assertEquals("teste@gmail.com", seAleatorio("teste@gmail.com"))
            }
        }
    }
}