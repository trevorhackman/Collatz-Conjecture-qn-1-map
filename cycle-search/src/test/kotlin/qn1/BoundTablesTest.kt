package qn1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoundTablesTest {
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
                16 to 6_250,
                17 to 5_882,
                18 to 5_555,
                19 to 5_263,
                20 to 5_000,
            ),
            THROUGH1_ROWS.associate { it.length to it.qBelowExponent },
        )
        assertEquals(
            mapOf(2 to 160_000, 3 to 2_333, 4 to 1_375, 5 to 900, 6 to 233, 7 to 135, 8 to 58, 9 to 35),
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
