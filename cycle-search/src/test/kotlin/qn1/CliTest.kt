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
    fun omittedOptionsTakeTheDefaults() {
        val any = parseCli(emptyArray())
        assertEquals(SearchMode.ANY, any.mode)
        assertEquals(DEFAULT_LENGTH, any.length)
        assertEquals(DEFAULT_MAX_H, any.maxH)

        val throughOne = parseCli(arrayOf("--through-1"))
        assertEquals(SearchMode.THROUGH_1, throughOne.mode)
        assertEquals(THROUGH1_DEFAULT_LENGTH, throughOne.length)
        assertEquals(THROUGH1_DEFAULT_MAX_H, throughOne.maxH)

        val fixed = parseCli(arrayOf("--q", "9"))
        assertEquals(SearchMode.FIXED_Q, fixed.mode)
        assertEquals(9, fixed.q)
        assertEquals(MIN_CYCLE_LENGTH, fixed.minLength)
        assertEquals(Q_DEFAULT_MAX_LENGTH, fixed.maxLength)

        val digits = parseCli(arrayOf("--digits"))
        assertEquals(SearchMode.DIGITS, digits.mode)
        assertEquals(DIGIT_PLACES, digits.places)

        val tables = parseCli(arrayOf("--tables", "through1"))
        assertEquals(SearchMode.TABLES, tables.mode)
        assertEquals("through1", tables.table)
        assertEquals(null, tables.length)
    }

    @Test
    fun anEvenMultiplierIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--q", "4"))
        }
    }

    @Test
    fun twoModesAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--q", "5", "--through-1"))
        }
    }

    @Test
    fun maxHNeedsALengthOnATable() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--tables", "any", "--max-h", "100"))
        }
    }

    @Test
    fun boundsNamesOneTable() {
        val cli = parseCli(arrayOf("--bounds", "q2", "--x", "1"))
        assertEquals(SearchMode.BOUNDS, cli.mode)
        assertEquals("q2", cli.bound)
        assertEquals(1, cli.x)
        for (name in listOf("q3star", "q30", "q2star")) {
            assertEquals(name, parseCli(arrayOf("--bounds", name)).bound)
        }
    }

    @Test
    fun boundsRejectsAnEvenElement() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--bounds", "q2", "--x", "2"))
        }
    }

    @Test
    fun boundsNeedsATable() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("--bounds", "all"))
        }
    }

    @Test
    fun aBareNumberIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            parseCli(arrayOf("1093", "200"))
        }
    }
}
