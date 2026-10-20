package qn1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CliTest {
    @Test
    fun fixedQNamesTheMultiplierAndTheLongestCycle() {
        val cli = parseCli(arrayOf("--q", "1093", "--max-length", "200"))
        assertEquals(SearchMode.FIXED_Q, cli.mode)
        assertEquals(1093, cli.q)
        assertEquals(2, cli.minLength)
        assertEquals(200, cli.maxLength)
    }

    @Test
    fun fixedQStartsAtTheGivenLength() {
        val cli = parseCli(arrayOf("--q", "1093", "--min-length", "201", "--max-length", "250"))
        assertEquals(201, cli.minLength)
        assertEquals(250, cli.maxLength)
    }

    @Test
    fun minLengthCannotPassMaxLength() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--q", "1093", "--min-length", "30", "--max-length", "20"))
        }
    }

    @Test
    fun equalsFormMatchesTheSpacedForm() {
        val spaced = parseCli(arrayOf("--length", "4", "--max-h", "200"))
        val equals = parseCli(arrayOf("--length=4", "--max-h=200"))
        assertEquals(spaced, equals)
        assertEquals(SearchMode.ANY, spaced.mode)
        assertEquals(4, spaced.length)
        assertEquals(200, spaced.maxH)
    }

    @Test
    fun aBareNumberIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("1093", "200"))
        }
    }
}
