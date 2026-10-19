package qn1

import java.math.BigInteger
import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DigitScanTest {
    @Test
    fun publishedPlaceCountIsTheAppendixScan() {
        assertEquals(1_072_000_000, DIGIT_PLACES)
        assertEquals(48, DIGIT_BLOCK)
        assertEquals(100, DIGIT_M_MIN)
        assertEquals(1_071_999_953, DIGIT_PLACES - DIGIT_BLOCK + 1)
        assertTrue(digitsFit(DIGIT_PLACES))
    }

    @Test
    fun divisionMatchesAPowerOfTwoDividend() {
        val random = Random(1)
        repeat(20) {
            val exponent = 500 + random.nextInt(4_000)
            val divisor = BigInteger(exponent / 2 + 8, random).setBit(exponent / 3)
            val expected = BigInteger.ONE.shiftLeft(exponent).divide(divisor)
            assertEquals(expected, floorDivPowerOfTwo(exponent.toLong(), divisor), "2^$exponent")
        }
        val exponent = 250_000
        val divisor = BigInteger(80_000, random).setBit(70_000)
        val expected = BigInteger.ONE.shiftLeft(exponent).divide(divisor)
        assertEquals(expected, floorDivPowerOfTwo(exponent.toLong(), divisor))
    }

    @Test
    fun cubeRootMatchesTheIntegerRoot() {
        for (exponent in listOf(0, 1, 2, 3, 10, 64, 189, 300, 1_000, 5_000)) {
            val expected = floorRoot(BigInteger.ONE.shiftLeft(exponent), 3)
            assertEquals(expected, floorCubeRootPowerOfTwo(exponent.toLong()), "2^$exponent")
        }
        val exponent = 200_000
        val expected = floorRoot(BigInteger.ONE.shiftLeft(exponent), 3)
        assertEquals(expected, floorCubeRootPowerOfTwo(exponent.toLong()))
    }

    @Test
    fun splitProductAgreesWithTheFullProduct() {
        val left = BigInteger("123456789012345678901234567890")
        val right = BigInteger("987654321098765432109876543210")
        val product = left.multiply(right)
        val width = maxOf(left.bitLength(), right.bitLength())
        val limit = width + width / 2L + 4
        assertTrue(left.bitLength() + right.bitLength() > limit)
        for (exponent in listOf(product.bitLength() - 2, product.bitLength() - 1, product.bitLength())) {
            val expected = Integer.signum(product.compareTo(BigInteger.ONE.shiftLeft(exponent)))
            val actual = Integer.signum(productComparedToPowerOfTwo(left, right, exponent.toLong(), limit))
            assertEquals(expected, actual, "2^$exponent")
        }
        assertEquals(
            0,
            productComparedToPowerOfTwo(BigInteger.ONE.shiftLeft(40), BigInteger.ONE.shiftLeft(50), 90, limit = 80),
        )
    }

    @Test
    fun scannerAgreesWithReadingEachPlace() {
        val places = 240
        for (r in 1..2) {
            val root = scanRootDigits(places, r)
            assertEquals(0, root.hits.count)
            val value = floorCubeRootPowerOfTwo(3L * places + r)
            for (m in DIGIT_M_MIN..(places - DIGIT_BLOCK)) {
                assertEquals(bruteBlock(value, places, m), false, "r=$r m=$m")
            }
        }
    }

    @Test
    fun scannerFindsAPlantedBlock() {
        val places = 200
        val m = 120
        val pattern = (BigInteger.ONE.shiftLeft(DIGIT_BLOCK) - BigInteger.ONE) / BigInteger.valueOf(3)
        val planted = BigInteger.ONE.shiftLeft(places).or(pattern.shiftLeft(places - (m + DIGIT_BLOCK)))
        val hits = alternatingBlocks(planted, places)
        assertEquals(1, hits.count)
        assertEquals(listOf(m), hits.first)
        assertTrue(bruteBlock(planted, places, m))
        assertEquals(false, bruteBlock(planted, places, m - 1))
    }
}

private fun bruteBlock(value: BigInteger, places: Int, m: Int): Boolean {
    var bits = 0L
    for (place in (m + 1)..(m + DIGIT_BLOCK)) {
        bits = (bits shl 1) or (if (value.testBit(places - place)) 1L else 0L)
    }
    val pattern = ((1L shl DIGIT_BLOCK) - 1) / 3
    return bits == pattern
}
