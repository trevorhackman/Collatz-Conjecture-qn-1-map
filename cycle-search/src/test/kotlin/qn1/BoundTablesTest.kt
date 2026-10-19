package qn1

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoundTablesTest {
    @Test
    fun log2OfPowersOfTwoIsExact() {
        assertEquals(0, log2(BigInteger.ONE).compareTo(BigDecimal.ZERO))
        assertEquals(0, log2(BigInteger.valueOf(8)).compareTo(BigDecimal(3)))
    }

    @Test
    fun length3StarMatchesThePrintedPowers() {
        val expected = mapOf(
            1 to 17990,
            3 to 17907,
            5 to 18214,
            7 to 18273,
            9 to 18094,
            11 to 18356,
            13 to 18387,
            15 to 18190,
            17 to 18439,
            19 to 18460,
            21 to 18255,
            23 to 18497,
            25 to 18514,
            27 to 18304,
            29 to 18543,
            31 to 18556,
            33 to 18344,
            35 to 18580,
        )
        for ((x, power) in expected) assertEquals(power, powerAbove(log2Q3Star(x)), "x=$x")
    }

    @Test
    fun length3StarAtFiveMatchesThePrintedLogarithm() {
        val log = log2Q3Star(5)
        val shown = log.setScale(12, java.math.RoundingMode.HALF_UP)
        assertEquals(BigDecimal("18213.724287504393"), shown)
        val (digits, count) = leadingDigits(log, 9)
        assertEquals("753951296", digits)
        assertEquals(5483, count)
        val atThree = log2Q3Star(3).setScale(6, java.math.RoundingMode.HALF_UP)
        assertEquals(BigDecimal("17906.510814"), atThree)
    }

    @Test
    fun length2StarMatchesThePrintedPowers() {
        assertEquals(11, powerAbove(log2Q2Star(1)))
        for (x in listOf(3, 5, 7, 9, 11)) assertEquals(8, powerAbove(log2Q2Star(x)), "x=$x")
        for (x in 13..35 step 2) assertEquals(7, powerAbove(log2Q2Star(x)), "x=$x")
    }

    @Test
    fun length2ScanMatchesThePrintedTable() {
        val expected = mapOf(
            1 to Triple(9, 7, 15),
            3 to Triple(12, 11, 18),
            5 to Triple(22, 17, 33),
            7 to Triple(12, 10, 18),
            9 to Triple(12, 11, 18),
            11 to Triple(13, 12, 19),
            13 to Triple(14, 9, 21),
            15 to Triple(22, 17, 32),
            17 to Triple(15, 13, 23),
            19 to Triple(17, 14, 25),
            21 to Triple(17, 12, 25),
            23 to Triple(16, 14, 23),
            25 to Triple(22, 17, 32),
            27 to Triple(15, 14, 22),
            29 to Triple(17, 16, 25),
            31 to Triple(17, 16, 25),
            33 to Triple(15, 13, 22),
            35 to Triple(22, 17, 32),
        )
        for ((x, row) in expected) {
            val (j0, winning, power) = row
            assertEquals(j0, j0Sqrt2(x, through = 40), "j0 x=$x")
            val hit = q20Hit(x, j0)
            assertEquals(winning, hit.j, "j x=$x")
            assertEquals(power, powerAbove(hit.log2), "power x=$x")
        }
        val atOne = q20Hit(1, 9).log2.setScale(6, java.math.RoundingMode.HALF_UP)
        assertEquals(BigDecimal("14.192568"), atOne)
    }

    @Test
    fun length3ZeroAtOneMatchesThePrintedPower() {
        val hit = q30Hit(1)
        assertEquals(11003, powerAbove(hit.log2))
        assertEquals(10998, hit.j)
        assertEquals(2, hit.r)
    }

    @Test
    fun length3ZeroAtTheOriginHasTheHandValue() {
        val log = log2Q30At(1, 0, 1).setScale(3, java.math.RoundingMode.HALF_UP)
        assertEquals(BigDecimal("3.277"), log)
    }

    @Test
    fun publishedSearchRowsMatchThePaper() {
        assertEquals(
            mapOf(
                3 to 66_666,
                4 to 25_000,
                5 to 20_000,
                6 to 16_666,
                7 to 14_285,
                8 to 12_500,
                9 to 11_111,
                10 to 10_000,
                11 to 9_090,
                12 to 8_333,
                13 to 7_692,
                14 to 7_142,
                15 to 6_666,
            ),
            THROUGH1_ROWS.associate { it.length to it.qBelowExponent },
        )
        assertEquals(
            mapOf(2 to 4_000, 3 to 1_333, 4 to 750, 5 to 400, 6 to 300, 7 to 100, 8 to 70, 9 to 30),
            ANY_ROWS.associate { it.length to it.qBelowExponent },
        )
        assertEquals(20, FIXED_Q_ROWS.first { it.first == 5 }.second)
        assertEquals((7..25 step 2).toList(), FIXED_Q_ROWS.filter { it.first != 5 }.map { it.first })
        assertTrue(FIXED_Q_ROWS.filter { it.first != 5 }.all { it.second == 16 })
    }

    @Test
    fun aShortThroughOneSweepPrintsThePowerOfTwo() {
        val row = LengthRow(3, 40)
        val text = formatSweep("through 1", row, searchThroughOne(3, 40))
        assertEquals("through 1 L=3 h<=40: 0 cycles, q below 2^13", text)
    }
}
