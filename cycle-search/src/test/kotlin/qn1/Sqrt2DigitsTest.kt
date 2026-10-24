package qn1

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Sqrt2DigitsTest {
    @Test
    fun publishedPlaceCountMatchesTheCubeScan() {
        assertEquals(DIGIT_PLACES, SQRT2_PLACES)
        assertEquals(1, SQRT2_M_MIN)
        assertEquals(1_073_741_822, maxSqrt2Places())
        assertTrue(sqrt2PlacesFit(SQRT2_PLACES))
    }

    @Test
    fun squareRootMatchesTheIntegerRoot() {
        for (exponent in listOf(0, 1, 2, 3, 10, 21, 64, 127, 300, 1_000, 5_000)) {
            val expected = floorRoot(BigInteger.ONE.shiftLeft(exponent), 2)
            assertEquals(expected, floorSqrtPowerOfTwo(exponent.toLong()), "2^$exponent")
        }
        val exponent = 200_000
        val expected = floorRoot(BigInteger.ONE.shiftLeft(exponent), 2)
        assertEquals(expected, floorSqrtPowerOfTwo(exponent.toLong()))
    }

    @Test
    fun expansionStartsWithTheKnownBits() {
        val places = 64
        val root = floorSqrtPowerOfTwo(2L * places + 1)
        assertEquals(places + 1, root.bitLength())
        val head = (1L shl 16) - 1
        val known = 0b0110101000001001L
        val bits = root.shiftRight(places - 16).and(BigInteger.valueOf(head)).toLong()
        assertEquals(known, bits)
    }

    @Test
    fun scannerAgreesWithReadingEachPlace() {
        val places = 240
        val root = floorSqrtPowerOfTwo(2L * places + 1)
        val hits = zeroRuns(root, places, mMin = 0)
        assertEquals(bruteLongest(root, places, 0), hits.longest)
        assertTrue(hits.longest >= 4)
    }

    @Test
    fun scannerFindsAPlantedRun() {
        val places = 200
        val m = 40
        val length = 17
        var planted = BigInteger.ONE.shiftLeft(places + 1).subtract(BigInteger.ONE)
        for (place in (m + 1)..(m + length)) {
            planted = planted.clearBit(places - place)
        }
        val found = zeroRuns(planted, places, mMin = 0)
        assertEquals(length, found.longest)
        assertEquals(listOf(m), found.first)
        assertEquals(1, found.count)
    }

    @Test
    fun aRunThatStartsBeforeTheWindowIsMeasuredFromTheWindow() {
        val places = 80
        val root = BigInteger.ONE.shiftLeft(places)
        val hits = zeroRuns(root, places, mMin = 30)
        assertEquals(places - 30, hits.longest)
        assertEquals(listOf(30), hits.first)
    }
}

private fun bruteLongest(root: BigInteger, places: Int, mMin: Int): Int {
    var longest = 0
    for (m in mMin until places) {
        var run = 0
        var place = m + 1
        while (place <= places && !root.testBit(places - place)) {
            run++
            place++
        }
        if (run > longest) longest = run
    }
    return longest
}
