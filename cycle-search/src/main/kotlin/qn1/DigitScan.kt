package qn1

import java.math.BigInteger
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

/** Binary places of 2^{1/3} and 2^{2/3} in the computations appendix. */
internal const val DIGIT_PLACES = 1_072_000_000

/** Alternating block 0,1,0,1,… of this length is the one named in Paper I. */
internal const val DIGIT_BLOCK = 48

/** First m whose places are scanned. */
internal const val DIGIT_M_MIN = 1

private const val NATIVE_DIV_LIMIT = 100_000L

/** Bits of divisor kept past the quotient length. The discarded tail moves the quotient by at most one. */
private const val QUOTIENT_GUARD = 96L

/**
 * A product whose bit length stays under this fits in one [BigInteger].
 * The published cube is about 2^3220000002, so the two halves of that product each sit just under this.
 */
private const val PRODUCT_BIT_LIMIT = Int.MAX_VALUE - 64L

private val THREE: BigInteger = BigInteger.valueOf(3)
private val PATTERN: Long = ((BigInteger.ONE.shiftLeft(DIGIT_BLOCK) - BigInteger.ONE) / THREE).toLong()

/** Alternating blocks of [DIGIT_BLOCK], and the first few starting places \(m\). */
internal data class BlockHits(val count: Int, val first: List<Int>)

/** Longest alternating run 0,1,0,1,…, and the starts m that attain it. */
internal data class AlternatingRunHits(val longest: Int, val count: Int, val first: List<Int>)

internal data class DigitRoot(
    val places: Int,
    val r: Int,
    val runs: AlternatingRunHits,
) {
    /** Power of two in the computations appendix: q < 2^{N-47}. */
    val qBelowExponent: Int get() = places - DIGIT_BLOCK + 1

    fun line(): String {
        val upper = places - 1
        val range = if (upper < DIGIT_M_MIN) {
            "no m >= $DIGIT_M_MIN"
        } else {
            "m from $DIGIT_M_MIN through $upper"
        }
        val where = if (runs.count == 0) {
            "longest alternating run 0"
        } else {
            "longest alternating run ${runs.longest} at m=${runs.first.joinToString(",")}" +
                if (runs.count > runs.first.size) " (${runs.count} total)" else ""
        }
        return "2^{$r/3}: $where for $range"
    }
}

internal fun runDigits(places: Int) {
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
    return DigitRoot(places, r, alternatingRuns(root, places))
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
        val zeros = x.getLowestSetBit()
        val body = if (zeros == 0) x else x.shiftRight(zeros)
        if (body.bitLength() >= 1_000_000) onProgress("squaring ${body.bitLength()} bits")
        val square = FastMul.square(body)
        val quotient = floorDivPowerOfTwo(work - 2L * zeros, square, onProgress, snap = false)
        x = x.shiftLeft(1).add(quotient).divide(THREE)
        x = snapCubeFloor(x, work)
    }
    return x
}

/**
 * Greatest integer q with q * [divisor] <= 2^exponent.
 * With [snap] false, the value may sit a few units off that floor.
 */
internal fun floorDivPowerOfTwo(
    exponent: Long,
    divisor: BigInteger,
    onProgress: (String) -> Unit = {},
    snap: Boolean = true,
): BigInteger {
    require(divisor.signum() > 0)
    require(exponent >= 0)
    val trailingZeros = divisor.getLowestSetBit()
    if (trailingZeros > 0) {
        if (exponent < trailingZeros.toLong()) return BigInteger.ZERO
        return floorDivPowerOfTwo(
            exponent - trailingZeros,
            divisor.shiftRight(trailingZeros),
            onProgress,
            snap,
        )
    }
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
    val approximate = coarseQuotient(reduced, high, onProgress)
    return if (snap) snapToFloorQuotient(approximate, divisor, exponent) else approximate
}

/**
 * Longest run of places m+1, m+2, … equal to 0,1,0,1,…, for m >= [mMin].
 * A start two places into a longer run is omitted: that run is shorter.
 * Place 1 is the bit just below the integer bit.
 */
internal fun alternatingRuns(
    root: BigInteger,
    places: Int,
    mMin: Int = DIGIT_M_MIN,
): AlternatingRunHits {
    require(root.signum() > 0)
    require(root.bitLength() == places + 1)
    require(mMin >= 0)
    val bytes = root.toByteArray()
    val padding = bytes.size * 8 - root.bitLength()
    fun bit(place: Int): Int {
        val index = padding + place
        val bite = bytes[index ushr 3].toInt() and 0xFF
        return (bite ushr (7 - (index and 7))) and 1
    }
    var longest = 0
    var count = 0
    val first = ArrayList<Int>(8)
    fun consider(m: Int, length: Int) {
        if (m < mMin || length <= 0) return
        if (length > longest) {
            longest = length
            count = 1
            first.clear()
            first += m
        } else if (length == longest) {
            count++
            if (first.size < 32) first += m
        }
    }
    var place = 1
    while (place <= places) {
        val bit = bit(place)
        val extends = place >= 3 && bit(place - 2) == 0 && bit(place - 1) == 1
        if (bit != 0 || extends) {
            place++
            continue
        }
        var length = 0
        while (place + length <= places && bit(place + length) == length % 2) length++
        consider(place - 1, length)
        place += length
    }
    return AlternatingRunHits(longest, count, first)
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

/** Quotient of 2^[exponent] / [divisor] before [snapToFloorQuotient]. Exact when the quotient is short. */
private fun coarseQuotient(
    exponent: Long,
    divisor: BigInteger,
    onProgress: (String) -> Unit,
): BigInteger {
    val divisorBits = divisor.bitLength()
    if (exponent <= divisorBits.toLong() || exponent <= NATIVE_DIV_LIMIT) {
        return BigInteger.ONE.shiftLeft(shiftAmount(exponent)).divide(divisor)
    }
    onProgress("division by a $divisorBits-bit integer")
    return newtonFloorDivPowerOfTwo(exponent, divisor, onProgress)
}

/**
 * Doubles the known quotient bits until [exponent] is reached.
 * A step that produces a q-bit quotient multiplies the top q + [QUOTIENT_GUARD] bits of [divisor].
 * [snapToFloorQuotient] turns the returned value into the floor.
 */
private fun newtonFloorDivPowerOfTwo(
    exponent: Long,
    divisor: BigInteger,
    onProgress: (String) -> Unit,
): BigInteger {
    val n = divisor.bitLength().toLong()
    if (exponent - n + 1 <= QUOTIENT_GUARD) return shortExactDiv(exponent, divisor)
    var k = n + QUOTIENT_GUARD - 1
    var x = shortExactDiv(k, divisor)
    while (k < exponent) {
        val k2 = minOf(exponent, 2 * k - n - 4)
        check(k2 > k) { "quotient of 2^$exponent stopped at $k bits" }
        val q2 = k2 - n + 1
        onProgress("quotient bits $q2")
        val kept = q2 + QUOTIENT_GUARD
        val (stepDivisor, stepK, stepK2) = if (n > kept) {
            val shift = n - kept
            Triple(divisor.shiftRight(shiftAmount(shift)), k - shift, k2 - shift)
        } else {
            Triple(divisor, k, k2)
        }
        x = newtonStep(stepDivisor, x, stepK, stepK2)
        check(x.bitLength().toLong() <= q2 + 8) { "quotient of 2^$exponent grew past $q2 bits" }
        k = k2
    }
    return x
}

/** One Newton step from a k-bit reciprocal to a k2-bit reciprocal of [divisor]. */
private fun newtonStep(divisor: BigInteger, x: BigInteger, k: Long, k2: Long): BigInteger {
    val dx = FastMul.multiply(divisor, x)
    val remainder = subtractFromPowerOfTwo(k, dx)
    val corr = FastMul.multiply(remainder, x).shiftRight(shiftAmount(2 * k - k2))
    return x.shiftLeft(shiftAmount(k2 - k)).add(corr)
}

/**
 * Moves [approximate] until it is the greatest integer whose product with [divisor] is at most 2^[exponent].
 * The Newton step lands within a few units of that floor.
 */
private fun snapToFloorQuotient(
    approximate: BigInteger,
    divisor: BigInteger,
    exponent: Long,
): BigInteger {
    var q = if (approximate.signum() > 0) approximate else BigInteger.ZERO
    var steps = 0
    if (q.signum() > 0 && q.bitLength().toLong() + divisor.bitLength() <= PRODUCT_BIT_LIMIT) {
        var product = FastMul.multiply(q, divisor)
        while (steps < 8) {
            if (compareToPowerOfTwo(product, exponent) > 0) {
                q -= BigInteger.ONE
                product -= divisor
                check(product.signum() >= 0) { "quotient of 2^$exponent stepped below 0" }
                steps++
                continue
            }
            val next = product + divisor
            if (compareToPowerOfTwo(next, exponent) <= 0) {
                q += BigInteger.ONE
                product = next
                steps++
                continue
            }
            return q
        }
    } else {
        while (steps < 8) {
            if (productComparedToPowerOfTwo(q, divisor, exponent) > 0) {
                q -= BigInteger.ONE
                steps++
                continue
            }
            if (productComparedToPowerOfTwo(q + BigInteger.ONE, divisor, exponent) <= 0) {
                q += BigInteger.ONE
                steps++
                continue
            }
            return q
        }
    }
    error("quotient of 2^$exponent is off by more than 8")
}

/** floor(2^exponent / divisor) when the quotient has at most a few hundred bits. */
private fun shortExactDiv(exponent: Long, divisor: BigInteger): BigInteger {
    val n = divisor.bitLength().toLong()
    if (exponent < n - 1) return BigInteger.ZERO
    if (exponent == n - 1L) {
        return if (divisor.bitCount() == 1) BigInteger.ONE else BigInteger.ZERO
    }
    if (divisor.bitCount() == 1) {
        return BigInteger.ONE.shiftLeft(shiftAmount(exponent - (n - 1)))
    }
    val grow = exponent - n
    val remainder = BigInteger.ONE.shiftLeft(shiftAmount(n)).subtract(divisor)
    val extra = if (grow == 0L) {
        BigInteger.ZERO
    } else {
        remainder.shiftLeft(shiftAmount(grow)).divide(divisor)
    }
    return BigInteger.ONE.shiftLeft(shiftAmount(grow)).add(extra)
}

private fun subtractFromPowerOfTwo(exponent: Long, value: BigInteger): BigInteger {
    require(value.signum() >= 0)
    if (value.signum() == 0) return BigInteger.ONE.shiftLeft(shiftAmount(exponent))
    if (value.bitLength().toLong() == exponent + 1L) {
        check(value.bitCount() == 1) { "value exceeds 2^$exponent" }
        return BigInteger.ZERO
    }
    check(value.bitLength().toLong() <= exponent) { "value exceeds 2^$exponent" }
    return BigInteger.ONE.shiftLeft(shiftAmount(exponent)).subtract(value)
}

/**
 * Moves [root] until x^3 <= 2^[exponent] < (x+1)^3.
 * One square is kept and adjusted. The next cube is x^3 + 3x^2 + 3x + 1, so it does not need its own product.
 */
private fun snapCubeFloor(root: BigInteger, exponent: Long): BigInteger {
    var x = root
    var square = FastMul.square(x)
    var steps = 0
    while (steps < 8) {
        val (cmp, nextCmp) = cubeVersusNext(x, square, exponent)
        if (cmp > 0) {
            square = square.subtract(x.shiftLeft(1)).add(BigInteger.ONE)
            x -= BigInteger.ONE
            steps++
            continue
        }
        if (nextCmp > 0) return x
        square = square.add(x.shiftLeft(1)).add(BigInteger.ONE)
        x += BigInteger.ONE
        steps++
    }
    error("cube root of 2^$exponent stayed off the root")
}

/** Signs of x^3 - 2^[exponent] and (x+1)^3 - 2^[exponent]. [limit] forces the split product in tests. */
internal fun cubeVersusNext(
    x: BigInteger,
    square: BigInteger,
    exponent: Long,
    limit: Long = PRODUCT_BIT_LIMIT,
): Pair<Int, Int> {
    val span = square.multiply(THREE).add(x.multiply(THREE)).add(BigInteger.ONE)
    if (x.signum() == 0 || x.bitLength().toLong() + square.bitLength() <= limit) {
        val cube = if (x.signum() == 0) BigInteger.ZERO else FastMul.multiply(x, square)
        return compareToPowerOfTwo(cube, exponent) to compareToPowerOfTwo(cube.add(span), exponent)
    }
    val minHigh = (x.bitLength().toLong() + square.bitLength() - limit).toInt()
    val maxHigh = (limit - x.bitLength()).toInt()
    check(minHigh in 1..maxHigh) { "cube does not fit in a BigInteger" }
    val split = square.bitLength() - minHigh
    val lowMask = BigInteger.ONE.shiftLeft(split) - BigInteger.ONE
    val lowProduct = FastMul.multiply(x, square.and(lowMask))
    val high = FastMul.multiply(x, square.shiftRight(split)) + lowProduct.shiftRight(split)
    val low = lowProduct.and(lowMask)
    val spanLow = span.and(lowMask)
    val carry = (low + spanLow).shiftRight(split)
    val nextHigh = high + span.shiftRight(split) + carry
    val nextLow = (low + spanLow).and(lowMask)
    return compareSplitToPower(high, low, exponent, split) to
        compareSplitToPower(nextHigh, nextLow, exponent, split)
}

/** Sign of high * 2^split + low compared with 2^[exponent], with 0 <= low < 2^split. */
private fun compareSplitToPower(high: BigInteger, low: BigInteger, exponent: Long, split: Int): Int {
    if (exponent < split) {
        if (high.signum() > 0) return 1
        return compareToPowerOfTwo(low, exponent)
    }
    val highCompared = compareToPowerOfTwo(high, exponent - split)
    if (highCompared != 0) return highCompared
    return if (low.signum() == 0) 0 else 1
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
        return compareToPowerOfTwo(FastMul.multiply(a, b), exponent)
    }
    val minHigh = (a.bitLength().toLong() + b.bitLength() - limit).toInt()
    val maxHigh = (limit - a.bitLength()).toInt()
    check(minHigh in 1..maxHigh) { "product does not fit in a BigInteger" }
    val split = b.bitLength() - minHigh
    val lowMask = BigInteger.ONE.shiftLeft(split) - BigInteger.ONE
    val lowProduct = FastMul.multiply(a, b.and(lowMask))
    val highProduct = FastMul.multiply(a, b.shiftRight(split))
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
