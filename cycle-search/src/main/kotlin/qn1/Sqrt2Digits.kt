package qn1

import java.math.BigInteger
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

/** Binary places of √2. Same machine limit as [DIGIT_PLACES]. */
internal const val SQRT2_PLACES = DIGIT_PLACES

/** First m whose places are scanned. */
internal const val SQRT2_M_MIN = 1

/** Longest zero run in the fractional bits, and the first starts m of the runs that attain it. */
internal data class ZeroRunHits(val longest: Int, val count: Int, val first: List<Int>)

internal data class Sqrt2Digits(
    val places: Int,
    val runs: ZeroRunHits,
) {
    /** Last m whose next [ZeroRunHits.longest] + 1 places lie inside the computed expansion. */
    val mUpper: Int get() = places - runs.longest - 1

    /**
     * A cycle satisfies δ < 1/x.
     * No run longer than [ZeroRunHits.longest] means δ ≥ 2^{-(longest+1)}, so x is below this power of two.
     */
    val xBelowExponent: Int get() = runs.longest + 1

    fun line(): String {
        val upper = places - 1
        val range = if (upper < SQRT2_M_MIN) {
            "no m >= $SQRT2_M_MIN"
        } else {
            "m from $SQRT2_M_MIN through $upper"
        }
        val where = if (runs.count == 0) {
            "longest zero run 0"
        } else {
            "longest zero run ${runs.longest} at m=${runs.first.joinToString(",")}" +
                if (runs.count > runs.first.size) " (${runs.count} total)" else ""
        }
        return "sqrt(2): $where for $range"
    }
}

internal fun runSqrt2(places: Int) {
    if (!sqrt2PlacesFit(places)) {
        System.err.println("places must be from 1 through ${maxSqrt2Places()}")
        exitProcess(1)
    }
    val ms = measureTimeMillis {
        val scan = scanSqrt2Digits(places) { System.err.println(it) }
        println(scan.line())
    }
    println("sqrt(2) digit scan in ${ms / 1000.0}s")
}

/** Largest `places` whose odd exponent 2·places+1 still fits in a shift. */
internal fun maxSqrt2Places(): Int = ((Int.MAX_VALUE - 3) / 2)

internal fun sqrt2PlacesFit(places: Int): Boolean = places in 1..maxSqrt2Places()

/**
 * Binary places 1 through [places] of √2.
 * The integer I = floor(2^places · √2) satisfies I^2 < 2^{2·places+1} < (I+1)^2.
 * Place 1 is the bit just below the integer bit. A zero run at m is places m+1, m+2, … of that expansion.
 */
internal fun scanSqrt2Digits(places: Int, onProgress: (String) -> Unit = {}): Sqrt2Digits {
    require(sqrt2PlacesFit(places))
    val exponent = 2L * places + 1
    val root = floorSqrtPowerOfTwo(exponent) { onProgress("sqrt(2) $it") }
    check(root.bitLength() == places + 1) { "sqrt(2) has bit length ${root.bitLength()}" }
    return Sqrt2Digits(places, zeroRuns(root, places))
}

/**
 * Greatest integer x with x^2 <= 2^exponent.
 * Each doubling of the known bits is corrected by Heron's iteration, then moved onto the floor.
 */
internal fun floorSqrtPowerOfTwo(exponent: Long, onProgress: (String) -> Unit = {}): BigInteger {
    require(exponent >= 0)
    if (exponent <= Int.MAX_VALUE && exponent / 2 + 1 <= 64) {
        return floorRoot(BigInteger.ONE.shiftLeft(exponent.toInt()), 2)
    }
    val finalBits = (exponent / 2).toInt() + 1
    var bits = 64
    var shift = finalBits - bits
    val seedExponent = exponent - 2L * shift
    var x = floorRoot(BigInteger.ONE.shiftLeft(seedExponent.toInt()), 2)
    while (shift > 0) {
        val add = minOf(shift.toLong(), bits.toLong()).toInt()
        x = x.shiftLeft(add)
        bits += add
        shift -= add
        val work = exponent - 2L * shift
        onProgress("root bits $bits")
        x = heronUntilClose(x, work, onProgress)
        x = snapSqrtFloor(x, work)
    }
    return x
}

/**
 * Zero runs in the fractional part of [root], for starts m >= [mMin].
 * [root] is floor(2^[places] · √2), so its high bit is the integer bit.
 * The reported length is the longest run whose first bit is place m+1.
 */
internal fun zeroRuns(
    root: BigInteger,
    places: Int,
    mMin: Int = SQRT2_M_MIN,
): ZeroRunHits {
    require(root.signum() > 0)
    require(root.bitLength() == places + 1)
    require(mMin >= 0)
    val bytes = root.toByteArray()
    val padding = bytes.size * 8 - root.bitLength()
    var run = 0
    var longest = 0
    var count = 0
    val first = ArrayList<Int>(8)
    fun consider(endPlace: Int) {
        if (run == 0) return
        val startPlace = endPlace - run + 1
        val startM = startPlace - 1
        val m = if (startM >= mMin) startM else mMin
        val length = endPlace - m
        if (length <= 0) return
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
    for (place in 1..places) {
        val index = padding + place
        val bite = bytes[index ushr 3].toInt() and 0xFF
        val bit = (bite ushr (7 - (index and 7))) and 1
        if (bit == 0) {
            run++
        } else {
            consider(place - 1)
            run = 0
        }
    }
    consider(places)
    return ZeroRunHits(longest, count, first)
}

/** Heron steps from a shifted root. Stops on the floor or the integer just below it. */
private fun heronUntilClose(
    start: BigInteger,
    exponent: Long,
    onProgress: (String) -> Unit,
): BigInteger {
    var x = if (start.signum() > 0) start else BigInteger.ONE
    repeat(8) {
        val quotient = floorDivPowerOfTwo(exponent, x, onProgress)
        val next = x.add(quotient).shiftRight(1)
        if (next == x || next == x.subtract(BigInteger.ONE)) return next
        x = if (next.signum() > 0) next else BigInteger.ONE
    }
    return x
}

/** Moves [root] until x^2 <= 2^[exponent] < (x+1)^2. */
private fun snapSqrtFloor(root: BigInteger, exponent: Long): BigInteger {
    var x = if (root.signum() > 0) root else BigInteger.ZERO
    var steps = 0
    while (steps < 8) {
        if (x.signum() > 0 && productComparedToPowerOfTwo(x, x, exponent) > 0) {
            x -= BigInteger.ONE
            steps++
            continue
        }
        val next = x.add(BigInteger.ONE)
        if (productComparedToPowerOfTwo(next, next, exponent) > 0) return x
        x = next
        steps++
    }
    error("square root of 2^$exponent stayed off the root")
}
