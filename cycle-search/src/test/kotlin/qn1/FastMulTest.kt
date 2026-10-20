package qn1

import java.math.BigInteger
import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class FastMulTest {
    @Test
    fun productMatchesTheIntegerProduct() {
        val random = Random(2)
        val widths = intArrayOf(1, 8, 16, 17, 31, 32, 64, 200, 4_096, 16_384, 50_000, 80_000)
        for (width in widths) {
            val left = BigInteger(width, random).setBit(width - 1)
            val right = BigInteger(width + 20, random).setBit(0)
            assertEquals(left.multiply(right), FastMul.multiply(left, right), "width $width")
            assertEquals(left.multiply(left), FastMul.square(left), "square $width")
        }
        val shifted = BigInteger("1234567890123456789012345678901234567890").shiftLeft(1000)
        val other = BigInteger("987654321098765432109876543210").shiftLeft(40)
        assertEquals(shifted.multiply(other), FastMul.multiply(shifted, other))
    }

    @Test
    fun divisionAboveTheNativeLimitMatches() {
        val exponent = 180_000L
        val divisor = BigInteger.ONE.shiftLeft(40_000).add(BigInteger.valueOf(3)).shiftLeft(25)
        val expected = BigInteger.ONE.shiftLeft(exponent.toInt()).divide(divisor)
        assertEquals(expected, floorDivPowerOfTwo(exponent, divisor))
        val power = BigInteger.ONE.shiftLeft(50_000)
        assertEquals(
            BigInteger.ONE.shiftLeft((exponent - 50_000).toInt()),
            floorDivPowerOfTwo(exponent, power),
        )
    }
}
