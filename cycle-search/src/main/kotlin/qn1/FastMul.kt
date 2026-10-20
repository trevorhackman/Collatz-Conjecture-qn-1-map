package qn1

import java.math.BigInteger
import java.util.Arrays
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.jvm.Synchronized

/**
 * Exact product of nonnegative integers.
 *
 * A short factor uses [BigInteger.multiply]. A long product is a length-2^k number-theoretic
 * transform: 16-bit digits, modulo 576460796326838273 = 536870953 * 2^30 + 1. At transform
 * length 2^28 every convolution sum still fits under that modulus, so the digits are exact.
 */
internal object FastMul {
    private const val DIGIT_BITS = 16
    private const val DIGIT_BASE = 1 shl DIGIT_BITS
    private const val MAX_LOG = 28
    private const val NTT_MIN_BITS = 16_384
    private const val PARALLEL_LENGTH = 4_096

    /** 536870953 * 2^30 + 1. The 2-power roots of unity exist through order 2^30. */
    private const val P = 576460796326838273L

    /** -P^{-1} mod 2^64, so Montgomery reduction can clear the low word. Bit pattern 0xf800000a3fffffff. */
    private const val P_INV = -576460708280008705L

    /** 2^128 mod P, the Montgomery square of the radix. */
    private const val R2 = 424992074480974849L

    /** Primitive 2^30-th root of unity: 3^536870953 mod P. */
    private const val OMEGA = 263559452451317276L

    private val workers = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
    private val pool = Executors.newFixedThreadPool(workers) { runnable ->
        Thread(runnable, "ntt").apply { isDaemon = true }
    }

    private var twiddles = LongArray(0)
    private var twiddleLength = 0
    private var bufA = LongArray(0)
    private var bufB = LongArray(0)

    @Synchronized
    fun multiply(left: BigInteger, right: BigInteger): BigInteger {
        if (left.signum() == 0 || right.signum() == 0) return BigInteger.ZERO
        require(left.signum() > 0 && right.signum() > 0)
        val leftZeros = left.getLowestSetBit()
        val rightZeros = right.getLowestSetBit()
        val a = if (leftZeros == 0) left else left.shiftRight(leftZeros)
        val b = if (rightZeros == 0) right else right.shiftRight(rightZeros)
        val product = multiplyOdd(a, b)
        val shift = leftZeros.toLong() + rightZeros
        return if (shift == 0L) product else product.shiftLeft(shiftAmount(shift))
    }

    @Synchronized
    fun square(value: BigInteger): BigInteger {
        if (value.signum() == 0) return BigInteger.ZERO
        require(value.signum() > 0)
        val zeros = value.getLowestSetBit()
        val body = if (zeros == 0) value else value.shiftRight(zeros)
        val squared = if (body.bitLength() < NTT_MIN_BITS) body.multiply(body) else transformProduct(body, body, square = true)
        return if (zeros == 0) squared else squared.shiftLeft(shiftAmount(zeros.toLong() * 2))
    }

    private fun multiplyOdd(left: BigInteger, right: BigInteger): BigInteger {
        if (left.bitLength() < NTT_MIN_BITS || right.bitLength() < NTT_MIN_BITS) {
            return left.multiply(right)
        }
        return transformProduct(left, right, square = false)
    }

    private fun transformProduct(left: BigInteger, right: BigInteger, square: Boolean): BigInteger {
        val leftDigits = toDigits(left)
        val rightDigits = if (square) leftDigits else toDigits(right)
        val terms = minOf(leftDigits.size, rightDigits.size).toLong()
        val maxDigit = DIGIT_BASE - 1L
        check(terms * maxDigit * maxDigit < P) { "product does not fit in the transform" }
        val n = transformLength(leftDigits.size + rightDigits.size)
        ensureTwiddles(n)
        bufA = load(bufA, n, leftDigits)
        forward(bufA, n)
        if (square) {
            pointwiseSquare(bufA, n)
        } else {
            bufB = load(bufB, n, rightDigits)
            forward(bufB, n)
            pointwise(bufA, bufB, n)
        }
        inverse(bufA, n)
        return fromDigits(bufA, n)
    }

    private fun transformLength(digits: Int): Int {
        var n = 1
        while (n < digits) {
            check(n < 1 shl MAX_LOG) { "product does not fit in the transform" }
            n = n shl 1
        }
        return n
    }

    private fun ensureTwiddles(n: Int) {
        if (twiddleLength == n) return
        val powers = LongArray(n ushr 1)
        var step = OMEGA
        val log = Integer.numberOfTrailingZeros(n)
        repeat(30 - log) { step = mulMod(step, step) }
        val root = toMont(step)
        val tasks = if (powers.size < PARALLEL_LENGTH) 1 else workers.coerceAtMost(powers.size)
        val chunk = (powers.size + tasks - 1) / tasks
        val starts = LongArray(tasks) { toMont(1L) }
        for (task in 1 until tasks) starts[task] = montPow(root, task.toLong() * chunk)
        parallel(tasks, force = true) { start, end ->
            for (task in start until end) {
                val from = task * chunk
                val to = minOf(powers.size, from + chunk)
                var value = starts[task]
                for (j in from until to) {
                    powers[j] = value
                    value = montMul(value, root)
                }
            }
        }
        twiddles = powers
        twiddleLength = n
    }

    private fun load(buffer: LongArray, n: Int, digits: IntArray): LongArray {
        val out = if (buffer.size < n) LongArray(n) else buffer
        Arrays.fill(out, 0, n, 0L)
        for (i in digits.indices) out[i] = toMont(digits[i].toLong())
        return out
    }

    private fun forward(a: LongArray, n: Int) {
        bitReverse(a, n)
        butterflies(a, n, inverse = false)
    }

    private fun inverse(a: LongArray, n: Int) {
        bitReverse(a, n)
        butterflies(a, n, inverse = true)
        val invN = toMont(invertPowerOfTwo(Integer.numberOfTrailingZeros(n)))
        parallel(n) { start, end ->
            for (i in start until end) a[i] = montMul(a[i], invN)
        }
    }

    private fun butterflies(a: LongArray, n: Int, inverse: Boolean) {
        var len = 2
        while (len <= n) {
            val step = n / len
            val half = len ushr 1
            parallel(n ushr 1) { start, end ->
                butterflySpan(a, n, len, half, step, inverse, start, end)
            }
            len = len shl 1
        }
    }

    /** Butterflies [start, end) in stage order, where each stage has n/2 independent butterflies. */
    private fun butterflySpan(
        a: LongArray,
        n: Int,
        len: Int,
        half: Int,
        step: Int,
        inverse: Boolean,
        start: Int,
        end: Int,
    ) {
        val halfN = n ushr 1
        val powers = twiddles
        var pos = start
        while (pos < end) {
            val block = pos / half
            val j0 = pos - block * half
            val j1 = minOf(half, j0 + (end - pos))
            val base = block * len
            if (!inverse) {
                for (j in j0 until j1) {
                    val u = a[base + j]
                    val v = montMul(a[base + j + half], powers[j * step])
                    val sum = u + v
                    a[base + j] = if (sum >= P) sum - P else sum
                    val diff = u - v
                    a[base + j + half] = if (diff < 0) diff + P else diff
                }
            } else {
                for (j in j0 until j1) {
                    val index = j * step
                    val w = if (index == 0) powers[0] else P - powers[halfN - index]
                    val u = a[base + j]
                    val v = montMul(a[base + j + half], w)
                    val sum = u + v
                    a[base + j] = if (sum >= P) sum - P else sum
                    val diff = u - v
                    a[base + j + half] = if (diff < 0) diff + P else diff
                }
            }
            pos += j1 - j0
        }
    }

    private fun pointwise(a: LongArray, b: LongArray, n: Int) {
        parallel(n) { start, end ->
            for (i in start until end) a[i] = montMul(a[i], b[i])
        }
    }

    private fun pointwiseSquare(a: LongArray, n: Int) {
        parallel(n) { start, end ->
            for (i in start until end) a[i] = montMul(a[i], a[i])
        }
    }

    private fun parallel(work: Int, force: Boolean = false, block: (Int, Int) -> Unit) {
        if (work <= 0) return
        if (workers == 1 || work == 1 || (!force && work < PARALLEL_LENGTH)) {
            block(0, work)
            return
        }
        val tasks = workers.coerceAtMost(work)
        val latch = CountDownLatch(tasks)
        val failure = AtomicReference<Throwable>()
        val chunk = (work + tasks - 1) / tasks
        repeat(tasks) { task ->
            val start = task * chunk
            val end = minOf(work, start + chunk)
            pool.execute {
                try {
                    if (start < end) block(start, end)
                } catch (error: Throwable) {
                    failure.compareAndSet(null, error)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        failure.get()?.let { throw it }
    }

    private fun bitReverse(a: LongArray, n: Int) {
        val log = Integer.numberOfTrailingZeros(n)
        if (log <= 1) return
        val shift = 32 - log
        parallel(n) { start, end ->
            for (i in start until end) {
                val j = Integer.reverse(i) ushr shift
                if (i < j) {
                    val tmp = a[i]
                    a[i] = a[j]
                    a[j] = tmp
                }
            }
        }
    }

    private fun fromDigits(a: LongArray, n: Int): BigInteger {
        val digits = IntArray(n + 4)
        var carry = 0L
        var last = 0
        for (i in 0 until n) {
            val sum = fromMont(a[i]) + carry
            digits[i] = (sum and 0xFFFF).toInt()
            carry = sum ushr 16
            if (digits[i] != 0) last = i + 1
        }
        var extra = n
        while (carry != 0L) {
            digits[extra] = (carry and 0xFFFF).toInt()
            if (digits[extra] != 0) last = extra + 1
            carry = carry ushr 16
            extra++
        }
        if (last == 0) return BigInteger.ZERO
        val bytes = ByteArray(last * 2)
        for (i in 0 until last) {
            val high = (last - 1 - i) * 2
            bytes[high] = (digits[i] ushr 8).toByte()
            bytes[high + 1] = digits[i].toByte()
        }
        var start = 0
        while (start < bytes.size - 1 && bytes[start].toInt() == 0) start++
        return BigInteger(1, if (start == 0) bytes else bytes.copyOfRange(start, bytes.size))
    }

    private fun toDigits(value: BigInteger): IntArray {
        val raw = value.toByteArray()
        val digits = IntArray((value.bitLength() + DIGIT_BITS - 1) ushr 4)
        val magStart = if (raw[0].toInt() == 0) 1 else 0
        val magLen = raw.size - magStart
        for (fromLow in 0 until magLen) {
            val bite = raw[raw.size - 1 - fromLow].toInt() and 0xFF
            val bit = fromLow * 8
            digits[bit ushr 4] = digits[bit ushr 4] or (bite shl (bit and 15))
        }
        return digits
    }

    private fun toMont(value: Long): Long = montMul(value, R2)

    private fun fromMont(value: Long): Long = redc(0L, value)

    private fun montMul(left: Long, right: Long): Long {
        return redc(Math.unsignedMultiplyHigh(left, right), left * right)
    }

    /** a*b mod P, both already reduced. */
    private fun mulMod(left: Long, right: Long): Long {
        val mont = montMul(left, right)
        return montMul(mont, R2)
    }

    private fun montPow(base: Long, exponent: Long): Long {
        var result = toMont(1L)
        var factor = base
        var bits = exponent
        while (bits > 0L) {
            if (bits and 1L == 1L) result = montMul(result, factor)
            factor = montMul(factor, factor)
            bits = bits ushr 1
        }
        return result
    }

    private fun invertPowerOfTwo(log: Int): Long {
        val inv2 = (P + 1) ushr 1
        var inv = 1L
        repeat(log) { inv = mulMod(inv, inv2) }
        return inv
    }

    /**
     * T * R^{-1} mod P, where T = hi * 2^64 + lo as unsigned words.
     * T must be less than P * 2^64.
     */
    private fun redc(hi: Long, lo: Long): Long {
        val m = lo * P_INV
        val mpHi = Math.unsignedMultiplyHigh(m, P)
        val mpLo = m * P
        val carry = if (java.lang.Long.compareUnsigned(lo + mpLo, lo) < 0) 1L else 0L
        var res = hi + mpHi + carry
        if (java.lang.Long.compareUnsigned(res, P) >= 0) res -= P
        if (java.lang.Long.compareUnsigned(res, P) >= 0) res -= P
        return res
    }
}

private fun shiftAmount(bits: Long): Int {
    require(bits in 0..Int.MAX_VALUE - 2L) { "shift $bits exceeds a BigInteger" }
    return bits.toInt()
}
