package qn1

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/** Decimal digits kept while the logarithms of the bounds are built. */
private val MC = MathContext(50)

private val LN2 = BigDecimal("0.69314718055994530941723212145817656807550013436026")
private val LOG2_10 = BigDecimal("3.32192809488736234787031942948939017586483139302458")
private val LOG10_2 = BigDecimal("0.30102999566398119521373889472449302676818988146211")
private val LOG2_3 = BigDecimal("1.58496250072115618145373894394781650875981440769248")

/** N in the cube-root measure. log2 of this number is μ_3 * 21. */
private val CUBE_N = BigInteger.valueOf(16_166_467_234L)

/** Three decimals, as printed for the square-root exponent. */
private val MU2 = BigDecimal("1.465")

/** Odd x in the Paper II tables run from 1 through this value. */
internal const val BOUNDS_MAX_X = 35

/** The length-3 table takes the maximum over j below this exponent. */
internal const val Q30_J_MAX = 11_000

/**
 * A later failure of the square-root measure is ruled out through this exponent.
 * It is the explicit k_0(2) from Bauer and Bennett (2002).
 */
internal const val SQRT2_SEARCH_LIMIT = 53_620

private val log2N: BigDecimal by lazy { log2(CUBE_N) }

/** One term (1+1/x)/ρ, with the exponent j that produced it. */
data class BoundHit(
    val log2: BigDecimal,
    val j: Int,
    val r: Int,
)

/** μ_3 = 3 log2(N) / 63. */
internal fun mu3(): BigDecimal = log2N.multiply(BigDecimal(3)).divide(BigDecimal(63), MC)

/** γ = 1 - μ + 1/μ. */
private fun gamma(mu: BigDecimal): BigDecimal =
    BigDecimal.ONE.subtract(mu).add(BigDecimal.ONE.divide(mu, MC))

/** c_3(s') = 2^{-5} N^{-79/63} / s'. This is its base-2 logarithm. */
private fun log2C3(sPrime: Int): BigDecimal {
    val pow = BigDecimal(79).divide(BigDecimal(63), MC).multiply(log2N, MC)
    return BigDecimal(-5).subtract(pow).subtract(log2(BigInteger.valueOf(sPrime.toLong())))
}

/** s'_near = x / gcd(x, 3). s'_win is x itself. */
internal fun sNear(x: Int): Int = x / gcdInt(x, 3)

private fun gcdInt(a: Int, b: Int): Int = if (b == 0) a else gcdInt(b, a % b)

/**
 * log2 of Q_{3,*}(x), the large-exponent length-3 bound.
 * The odd part in c_3 on the near miss is [sNear], and on the window it is x.
 */
fun log2Q3Star(x: Int): BigDecimal {
    require(x >= 1 && x % 2 == 1)
    val mu = mu3()
    val gamma = gamma(mu)
    val logX = log2(BigInteger.valueOf(x.toLong()))
    val log1px = log2(BigInteger.valueOf((x + 1).toLong())).subtract(logX)
    val third = BigDecimal.ONE.divide(BigDecimal(3), MC)
    val twoThirds = BigDecimal(2).divide(BigDecimal(3), MC)
    val logA = logX
        .subtract(BigDecimal.ONE)
        .add(third)
        .add(log2C3(sNear(x)).subtract(twoThirds).subtract(log1px).divide(mu, MC))
    val a3 = exp2(logA)
    val num = BigDecimal(2).divide(BigDecimal(3).multiply(a3, MC), MC).add(BigDecimal(3))
    val logDen = log2C3(x).add(BigDecimal.ONE).subtract(mu)
    return log2(num).subtract(logDen).divide(gamma, MC)
}

/**
 * log2 of Q_{2,*}(x). μ_2 is [MU2], three decimals, and the constant in front of the measure is 1.
 */
fun log2Q2Star(x: Int): BigDecimal {
    require(x >= 1 && x % 2 == 1)
    val mu = MU2
    val gamma = gamma(mu)
    val logX = log2(BigInteger.valueOf(x.toLong()))
    val log1px = log2(BigInteger.valueOf((x + 1).toLong())).subtract(logX)
    val half = BigDecimal("0.5")
    val logA = logX.subtract(half).subtract(half.add(log1px).divide(mu, MC))
    val a2 = exp2(logA)
    val num = BigDecimal.ONE.divide(BigDecimal(2).multiply(a2, MC), MC).add(BigDecimal(2))
    val logDen = BigDecimal.ONE.subtract(mu)
    return log2(num).subtract(logDen).divide(gamma, MC)
}

/**
 * The odd y closest to x 2^{j+r/3} gives one value of (1+1/x)/ρ.
 * Returns the base-2 logarithm of that value. j runs from 0, and r is 1 or 2.
 */
fun log2Q30At(x: Int, j: Int, r: Int): BigDecimal {
    require(x >= 1 && x % 2 == 1)
    require(j >= 0 && (r == 1 || r == 2))
    val xb = BigInteger.valueOf(x.toLong())
    val t = xb.pow(3).shiftLeft(3 * j + r)
    val floor = floorRoot(t, 3)
    val y = if (floor.testBit(0)) floor else floor.add(BigInteger.ONE)
    val diff = y.pow(3).subtract(t).abs()
    // root * (y^2 + y root + root^2) = 3 y^3 * (1 + O(2^{-j})), and |y - root| < 1.
    // Past j = 64 that factor does not move a displayed decimal of the logarithm.
    val approx = log2(BigInteger.valueOf((x + 1).toLong()))
        .subtract(log2(xb))
        .add(LOG2_3)
        .add(log2(y).multiply(BigDecimal(3)))
        .subtract(log2(diff))
    if (j >= 64) return approx
    val guard = 64
    val rootScaled = floorRoot(t.shiftLeft(3 * guard), 3)
    val w = y.pow(2).multiply(rootScaled).shiftLeft(guard)
        .add(y.multiply(rootScaled.pow(2)))
        .add(t.shiftLeft(2 * guard))
    return log2(BigInteger.valueOf((x + 1).toLong()))
        .subtract(log2(xb))
        .add(log2(w))
        .subtract(log2(diff))
        .subtract(BigDecimal(2 * guard))
}

/**
 * Maximum of (1+1/x)/ρ over r in {1, 2} and 0 ≤ j < [jMax].
 * Only the odd y closest to x 2^{j+r/3} can attain it.
 */
fun q30Hit(x: Int, jMax: Int = Q30_J_MAX, onProgress: (Int) -> Unit = {}): BoundHit {
    require(jMax > 0)
    val none = BoundHit(BigDecimal("-1000000"), -1, 0)
    return parallelChunks(jMax) { start, end ->
        onProgress(start)
        var best = none
        for (j in start until end) {
            for (r in intArrayOf(1, 2)) {
                val log = log2Q30At(x, j, r)
                if (log > best.log2) best = BoundHit(log, j, r)
            }
        }
        best
    }.maxBy { it.log2 }
}

/**
 * log2 of (1+1/x)/ρ at the odd y closest to x 2^{j+1/2}.
 */
fun log2Q20At(x: Int, j: Int): BigDecimal {
    require(x >= 1 && x % 2 == 1 && j >= 0)
    val xb = BigInteger.valueOf(x.toLong())
    val n = xb.pow(2).shiftLeft(2 * j + 1)
    val floor = n.sqrt()
    val y = if (floor.testBit(0)) floor else floor.add(BigInteger.ONE)
    val diff = y.pow(2).subtract(n).abs()
    val guard = 64
    val rootScaled = n.shiftLeft(2 * guard).sqrt()
    val w = n.shiftLeft(guard).add(y.multiply(rootScaled))
    return log2(BigInteger.valueOf((x + 1).toLong()))
        .subtract(log2(xb))
        .add(log2(w))
        .subtract(log2(diff))
        .subtract(BigDecimal(guard))
}

/**
 * True when some integer p has |√2 - p/(x 2^j)| ≤ 2^{-μ_2 j}.
 * Those j are exactly the ones excluded from the range where the measure is applied.
 */
fun sqrt2MeasureFails(x: Int, j: Int): Boolean {
    require(x >= 1 && j >= 0)
    val xb = BigInteger.valueOf(x.toLong())
    val n = xb.pow(2).shiftLeft(2 * j + 1)
    val s = n.sqrt()
    val rem = n.subtract(s.multiply(s))
    val thresh = log2(xb).subtract(BigDecimal("0.465").multiply(BigDecimal(j), MC))
    val above = s.shiftLeft(1).add(BigInteger.ONE).subtract(rem)
    return withinSqrt(rem, s, s, n, thresh) || withinSqrt(above, s.add(BigInteger.ONE), s, n, thresh)
}

/**
 * |√N - endpoint| = rem / (√N + endpoint), and √N lies strictly between [floorSqrt] and [floorSqrt]+1.
 * The two integer denominators around √N + endpoint decide the comparison unless the threshold sits between them.
 */
private fun withinSqrt(
    rem: BigInteger,
    endpoint: BigInteger,
    floorSqrt: BigInteger,
    n: BigInteger,
    thresh: BigDecimal,
): Boolean {
    if (rem.signum() == 0) return true
    val upperDen = floorSqrt.add(endpoint)
    val upper = log2(rem).subtract(log2(upperDen))
    if (upper <= thresh) return true
    val lower = log2(rem).subtract(log2(upperDen.add(BigInteger.ONE)))
    if (lower > thresh) return false
    val guard = 32
    val root = n.shiftLeft(2 * guard).sqrt()
    val den = root.add(endpoint.shiftLeft(guard))
    val exact = log2(rem).add(BigDecimal(guard)).subtract(log2(den))
    return exact <= thresh
}

/**
 * Least j0 such that the square-root measure holds for every j with j0 ≤ j < [through].
 * The scan starts at 0, and j = 0 fails for every odd x.
 */
fun j0Sqrt2(x: Int, through: Int = SQRT2_SEARCH_LIMIT): Int {
    require(through > 0)
    val lastFail = parallelChunks(through) { start, end ->
        var last = -1
        for (j in start until end) {
            if (sqrt2MeasureFails(x, j)) last = j
        }
        last
    }.max()
    return lastFail + 1
}

private fun <T> parallelChunks(limit: Int, block: (Int, Int) -> T): List<T> {
    val threads = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
    val chunk = (limit / (threads * 4)).coerceAtLeast(1)
    val pool = Executors.newFixedThreadPool(threads)
    try {
        val tasks = mutableListOf<Callable<T>>()
        var start = 0
        while (start < limit) {
            val end = (start + chunk).coerceAtMost(limit)
            val from = start
            tasks += Callable { block(from, end) }
            start = end
        }
        return pool.invokeAll(tasks).map { it.get() }
    } finally {
        pool.shutdown()
    }
}

/** Maximum of (1+1/x)/ρ over 0 ≤ j < [j0], at the closest odd integer. */
fun q20Hit(x: Int, j0: Int): BoundHit {
    require(j0 > 0)
    var best = BoundHit(BigDecimal("-1000000"), -1, 1)
    for (j in 0 until j0) {
        val log = log2Q20At(x, j)
        if (log > best.log2) best = BoundHit(log, j, 1)
    }
    return best
}

/** 2 raised to this power is the table entry above the positive real with that logarithm. */
internal fun powerAbove(log: BigDecimal): Int {
    val floor = log.setScale(0, RoundingMode.FLOOR)
    val frac = log.subtract(floor)
    val exp = floor.intValueExact()
    return if (frac.signum() == 0) exp else exp + 1
}

/** First [digits] of the integer part of 2^[log2Value], and how many digits that integer has. */
internal fun leadingDigits(log2Value: BigDecimal, digits: Int): Pair<String, Int> {
    val log10 = log2Value.multiply(LOG10_2, MC)
    val floor = log10.setScale(0, RoundingMode.FLOOR)
    val frac = log10.subtract(floor)
    var head = exp(frac.multiply(ln10(), MC))
    var digitsFloor = floor
    if (head >= BigDecimal.TEN) {
        head = head.movePointLeft(1)
        digitsFloor = digitsFloor.add(BigDecimal.ONE)
    }
    val shifted = head.movePointRight(digits - 1).setScale(0, RoundingMode.DOWN)
    val count = digitsFloor.intValueExact() + 1
    return shifted.toPlainString().take(digits) to count
}

internal fun log2(n: BigInteger): BigDecimal {
    require(n.signum() > 0)
    val exp = n.bitLength() - 1
    val keep = MC.precision * 4
    val excess = (n.bitLength() - keep).coerceAtLeast(0)
    val top = n.shiftRight(excess)
    val m = BigDecimal(top).divide(BigDecimal(BigInteger.ONE.shiftLeft(exp - excess)), MC)
    return BigDecimal(exp).add(log2OfMantissa(m))
}

private fun log2(z: BigDecimal): BigDecimal {
    require(z.signum() > 0)
    return log2(z.unscaledValue()).subtract(BigDecimal(z.scale()).multiply(LOG2_10, MC))
}

private fun log2OfMantissa(m: BigDecimal): BigDecimal {
    val z = m.subtract(BigDecimal.ONE).divide(m.add(BigDecimal.ONE), MC)
    val z2 = z.multiply(z, MC)
    var zPow = z
    var sum = BigDecimal.ZERO
    val eps = BigDecimal.ONE.scaleByPowerOfTen(-(MC.precision - 2))
    for (k in 0 until MC.precision * 2) {
        val term = zPow.divide(BigDecimal(2 * k + 1), MC)
        sum = sum.add(term, MC)
        if (term.abs() < eps) break
        zPow = zPow.multiply(z2, MC)
    }
    return sum.multiply(BigDecimal(2), MC).divide(LN2, MC)
}

private fun exp2(e: BigDecimal): BigDecimal {
    val floor = e.setScale(0, RoundingMode.FLOOR)
    val frac = e.subtract(floor)
    val pow = exp(frac.multiply(LN2, MC))
    val n = floor.intValueExact()
    return if (n >= 0) {
        pow.multiply(BigDecimal(BigInteger.ONE.shiftLeft(n)))
    } else {
        pow.divide(BigDecimal(BigInteger.ONE.shiftLeft(-n)), MC)
    }
}

private fun exp(x: BigDecimal): BigDecimal {
    var term = BigDecimal.ONE
    var sum = BigDecimal.ONE
    val eps = BigDecimal.ONE.scaleByPowerOfTen(-(MC.precision - 2))
    var n = 1
    while (n < MC.precision * 4) {
        term = term.multiply(x, MC).divide(BigDecimal(n), MC)
        sum = sum.add(term, MC)
        if (term.abs() < eps) break
        n++
    }
    return sum
}

private fun ln10(): BigDecimal = LOG2_10.multiply(LN2, MC)
