package qn1

import java.math.BigInteger
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

/** Binary places of 2^{1/3} and 2^{2/3} in the computations appendix. */
internal const val DIGIT_PLACES = 1_072_000_000

/** Alternating block required once m >= 100. */
internal const val DIGIT_BLOCK = 48

/** Smaller m is covered by the search over h. */
internal const val DIGIT_M_MIN = 100

private const val NATIVE_DIV_LIMIT = 100_000L

/**
 * A product whose bit length stays under this fits in one [BigInteger].
 * The published cube is about 2^3220000002, so the two halves of that product each sit just under this.
 */
private const val PRODUCT_BIT_LIMIT = Int.MAX_VALUE - 64L

private val THREE: BigInteger = BigInteger.valueOf(3)
private val PATTERN: Long = ((BigInteger.ONE.shiftLeft(DIGIT_BLOCK) - BigInteger.ONE) / THREE).toLong()

/** Alternating blocks of [DIGIT_BLOCK], and the first few starting places \(m\). */
internal data class BlockHits(val count: Int, val first: List<Int>)

internal data class DigitRoot(
    val places: Int,
    val r: Int,
    val hits: BlockHits,
) {
    /** Power of two in the computations appendix: q < 2^{N-47}. */
    val qBelowExponent: Int get() = places - DIGIT_BLOCK + 1

    fun line(): String {
        val upper = places - DIGIT_BLOCK
        val range = if (upper < DIGIT_M_MIN) {
            "no m >= $DIGIT_M_MIN"
        } else {
            "m from $DIGIT_M_MIN through $upper"
        }
        val blocks = if (hits.count == 0) {
            "0 alternating blocks"
        } else {
            "alternating blocks at m=${hits.first.joinToString(",")}" +
                if (hits.count > hits.first.size) " (${hits.count} total)" else ""
        }
        val bound = if (hits.count == 0 && upper >= DIGIT_M_MIN) ", q below 2^$qBelowExponent" else ""
        return "2^{$r/3}: verified, $blocks of length $DIGIT_BLOCK for $range$bound"
    }
}

internal fun runDigits(args: Array<String>) {
    val numbers = args.drop(1)
    if (numbers.size > 1) usage()
    val places = if (numbers.isEmpty()) DIGIT_PLACES else numbers[0].toIntOrNull() ?: usage()
    if (places < 1 || !digitsFit(places)) {
        System.err.println("places must be from 1 through ${maxDigitPlaces()}")
        exitProcess(1)
    }
    val ms = measureTimeMillis {
        for (r in 1..2) {
            val root = scanRootDigits(places, r) { System.err.println(it) }
            println(root.line())
        }
    }
    println("digit scan in ${ms / 1000.0}s")
}

/** Largest `places` whose square and quotient still fit in a [BigInteger]. */
internal fun maxDigitPlaces(): Int = ((Int.MAX_VALUE - 80) / 2)

internal fun digitsFit(places: Int): Boolean = places in 1..maxDigitPlaces()

/**
 * Binary places 1 through [places] of 2^{r/3}, with r equal to 1 or 2.
 * The integer I = floor(2^places * 2^{r/3}) satisfies I^3 < 2^{3*places+r} < (I+1)^3.
 * An alternating block is places m+1 through m+48 equal to 0,1,0,1,..., beginning with 0.
 */
internal fun scanRootDigits(places: Int, r: Int, onProgress: (String) -> Unit = {}): DigitRoot {
    require(r == 1 || r == 2)
    require(digitsFit(places))
    val exponent = 3L * places + r
    val root = floorCubeRootPowerOfTwo(exponent) { onProgress("2^{$r/3} $it") }
    check(root.bitLength() == places + 1) { "2^{$r/3} has bit length ${root.bitLength()}" }
    check(cubeComparedToPowerOfTwo(root, exponent) < 0)
    check(cubeComparedToPowerOfTwo(root + BigInteger.ONE, exponent) > 0)
    return DigitRoot(places, r, alternatingBlocks(root, places))
}

/**
 * Greatest integer x with x^3 <= 2^exponent.
 * The exponent is at most 3 * [maxDigitPlaces] + 2, so x and x^2 fit in a [BigInteger] and x^3 does not.
 */
internal fun floorCubeRootPowerOfTwo(exponent: Long, onProgress: (String) -> Unit = {}): BigInteger {
    require(exponent >= 0)
    if (exponent <= Int.MAX_VALUE && (exponent / 3).toInt() + 1 <= 64) {
        return floorRoot(BigInteger.ONE.shiftLeft(exponent.toInt()), 3)
    }
    val finalBits = (exponent / 3).toInt() + 1
    var bits = 64
    var shift = finalBits - bits
    var x = floorRoot(BigInteger.ONE.shiftLeft((exponent - 3L * shift).toInt()), 3)
    while (shift > 0) {
        val add = minOf(shift.toLong(), bits.toLong()).toInt()
        x = x.shiftLeft(add)
        bits += add
        shift -= add
        val work = exponent - 3L * shift
        onProgress("root bits $bits")
        val quotient = floorDivPowerOfTwo(work, x.multiply(x), onProgress)
        x = x.shiftLeft(1).add(quotient).divide(THREE)
        x = snapCubeFloor(x, work)
    }
    return x
}

/** Greatest integer q with q * [divisor] <= 2^exponent. */
internal fun floorDivPowerOfTwo(
    exponent: Long,
    divisor: BigInteger,
    onProgress: (String) -> Unit = {},
): BigInteger {
    require(divisor.signum() > 0)
    require(exponent >= 0)
    val divisorBits = divisor.bitLength()
    if (exponent < divisorBits - 1L) return BigInteger.ZERO
    if (exponent <= NATIVE_DIV_LIMIT) {
        return BigInteger.ONE.shiftLeft(exponent.toInt()).divide(divisor)
    }
    val quotientBits = exponent - divisorBits + 1
    val kept = minOf(divisorBits.toLong(), quotientBits + 64).toInt()
    val dropped = divisorBits - kept
    val high = if (dropped == 0) divisor else divisor.shiftRight(dropped)
    val reduced = exponent - dropped
    val approximate = exactFloorDivPowerOfTwo(reduced, high, onProgress)
    if (productComparedToPowerOfTwo(approximate, divisor, exponent) <= 0) return approximate
    val previous = approximate - BigInteger.ONE
    check(productComparedToPowerOfTwo(previous, divisor, exponent) <= 0) {
        "quotient of 2^$exponent is off by more than 1"
    }
    return previous
}

/**
 * Places m+1 through m+[block] of the fractional part, read from the high place.
 * Place 1 is the bit just below the integer bit.
 */
internal fun alternatingBlocks(
    root: BigInteger,
    places: Int,
    block: Int = DIGIT_BLOCK,
    mMin: Int = DIGIT_M_MIN,
): BlockHits {
    require(block in 1..48)
    require(root.signum() > 0)
    require(root.bitLength() == places + 1)
    val pattern = if (block == DIGIT_BLOCK) {
        PATTERN
    } else {
        ((BigInteger.ONE.shiftLeft(block) - BigInteger.ONE) / THREE).toLong()
    }
    val mask = (1L shl block) - 1
    val bytes = root.toByteArray()
    val padding = bytes.size * 8 - root.bitLength()
    var window = 0L
    var count = 0
    val first = ArrayList<Int>(8)
    val upper = places - block
    for (place in 1..places) {
        val index = padding + place
        val bite = bytes[index ushr 3].toInt() and 0xFF
        val bit = (bite ushr (7 - (index and 7))) and 1
        window = ((window shl 1) and mask) or bit.toLong()
        if (place < block) continue
        val m = place - block
        if (m < mMin || m > upper) continue
        if (window != pattern) continue
        count++
        if (first.size < 32) first += m
    }
    return BlockHits(count, first)
}

private fun exactFloorDivPowerOfTwo(
    exponent: Long,
    divisor: BigInteger,
    onProgress: (String) -> Unit,
): BigInteger {
    val divisorBits = divisor.bitLength()
    if (exponent <= divisorBits.toLong() || exponent <= NATIVE_DIV_LIMIT) {
        return BigInteger.ONE.shiftLeft(shiftAmount(exponent)).divide(divisor)
    }
    val base = BigInteger.ONE.shiftLeft(divisorBits)
    val quotient = base.divide(divisor)
    val remainder = base - quotient.multiply(divisor)
    check(remainder.signum() >= 0)
    val grow = exponent - divisorBits
    onProgress("division by a $divisorBits-bit integer")
    val extra = remainder.shiftLeft(shiftAmount(grow)).divide(divisor)
    return quotient.shiftLeft(shiftAmount(grow)).add(extra)
}

private fun snapCubeFloor(root: BigInteger, exponent: Long): BigInteger {
    var x = root
    var steps = 0
    while (cubeComparedToPowerOfTwo(x, exponent) > 0) {
        x -= BigInteger.ONE
        check(++steps <= 8) { "cube root of 2^$exponent stayed above the root" }
    }
    while (cubeComparedToPowerOfTwo(x + BigInteger.ONE, exponent) <= 0) {
        x += BigInteger.ONE
        check(++steps <= 8) { "cube root of 2^$exponent stayed below the root" }
    }
    return x
}

/** Negative when x^3 < 2^exponent, zero when equal, positive when greater. */
private fun cubeComparedToPowerOfTwo(root: BigInteger, exponent: Long): Int {
    return productComparedToPowerOfTwo(root, root.multiply(root), exponent)
}

/**
 * Negative when the product is below 2^exponent, zero when equal, positive when greater.
 * [limit] is the most bits one [BigInteger] product may use. Tests pass a small limit to force the split.
 */
internal fun productComparedToPowerOfTwo(
    left: BigInteger,
    right: BigInteger,
    exponent: Long,
    limit: Long = PRODUCT_BIT_LIMIT,
): Int {
    require(left.signum() >= 0 && right.signum() >= 0)
    if (left.signum() == 0 || right.signum() == 0) return -1
    if (left.bitLength().toLong() + right.bitLength() <= exponent) return -1
    val (a, b) = if (left.bitLength() <= right.bitLength()) left to right else right to left
    if (a.bitLength().toLong() + b.bitLength() <= limit) {
        return compareToPowerOfTwo(a.multiply(b), exponent)
    }
    val minHigh = (a.bitLength().toLong() + b.bitLength() - limit).toInt()
    val maxHigh = (limit - a.bitLength()).toInt()
    check(minHigh in 1..maxHigh) { "product does not fit in a BigInteger" }
    val split = b.bitLength() - minHigh
    val lowMask = BigInteger.ONE.shiftLeft(split) - BigInteger.ONE
    val lowProduct = a.multiply(b.and(lowMask))
    val highProduct = a.multiply(b.shiftRight(split))
    val highTotal = highProduct + lowProduct.shiftRight(split)
    val highCompared = compareToPowerOfTwo(highTotal, exponent - split)
    if (highCompared != 0) return highCompared
    return if (lowProduct.and(lowMask).signum() == 0) 0 else 1
}

private fun compareToPowerOfTwo(value: BigInteger, exponent: Long): Int {
    if (value.signum() == 0) return -1
    val bits = value.bitLength().toLong()
    if (bits < exponent + 1) return -1
    if (bits > exponent + 1) return 1
    return if (value.bitCount() == 1) 0 else 1
}

private fun shiftAmount(bits: Long): Int {
    require(bits in 0..Int.MAX_VALUE - 2) { "shift $bits exceeds a BigInteger" }
    return bits.toInt()
}
