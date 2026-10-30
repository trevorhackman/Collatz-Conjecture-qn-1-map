package qn1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CliTest {
    @Test
    fun fixedQNamesTheMultiplierAndTheLongestCycle() {
        val cli = parseCli(arrayOf("--q", "1093", "--max-length", "200", "--checked-odds", "100"))
        assertEquals(SearchMode.FIXED_Q, cli.mode)
        assertEquals(1093, cli.q)
        assertEquals(2, cli.minLength)
        assertEquals(200, cli.maxLength)
        assertEquals(100, cli.checkedOdds)
    }

    @Test
    fun fixedQStartsAtTheGivenLength() {
        val cli = parseCli(arrayOf("--q", "1093", "--min-length", "201", "--max-length", "250", "--checked-odds", "40"))
        assertEquals(201, cli.minLength)
        assertEquals(250, cli.maxLength)
    }

    @Test
    fun minLengthCannotPassMaxLength() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--q", "1093", "--min-length", "30", "--max-length", "20", "--checked-odds", "10"))
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
    fun omittedBoundsAreRejected() {
        assertFailsWith<IllegalArgumentException> { parseCli(emptyArray()) }
        assertFailsWith<IllegalArgumentException> { parseCli(arrayOf("--through-1")) }
        assertFailsWith<IllegalArgumentException> { parseCli(arrayOf("--q", "9")) }
        assertFailsWith<IllegalArgumentException> { parseCli(arrayOf("--q", "9", "--max-length", "10")) }
        assertFailsWith<IllegalArgumentException> { parseCli(arrayOf("--digits", "cbrt2")) }

        val sqrt2 = parseCli(arrayOf("--digits", "sqrt2", "--places", "1000"))
        assertEquals(SearchMode.DIGITS, sqrt2.mode)
        assertEquals("sqrt2", sqrt2.digits)
        assertEquals(1000, sqrt2.places)
    }

    @Test
    fun anEvenMultiplierIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--q", "4", "--max-length", "10", "--checked-odds", "1"))
        }
    }

    @Test
    fun twoModesAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--q", "5", "--through-1"))
        }
    }

    @Test
    fun digitsNeedsARoot() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--digits", "all"))
        }
    }

    @Test
    fun aBareNumberIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("1093", "200"))
        }
    }
}
