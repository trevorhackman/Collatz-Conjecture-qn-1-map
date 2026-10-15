package qn1

import java.math.BigInteger
import kotlin.system.measureTimeMillis

/**
 * Cycles of the odd-to-odd map `C(n) = (q n + 1) / 2^v`, `q` odd and at least 3.
 *
 * An `L`-cycle of odd positive integers uses `h` halvings with `L` not dividing `h`,
 * and `q = floor(2^{h/L})`. That `q` is odd: an even floor leaves no odd candidate
 * inside `q^L < 2^h < (q+1)^L`. The element `x` at the start of the cycle is
 * `N / (2^h - q^L)`, where `N` is the numerator fixed by the first `L-1` halving counts.
 *
 * Args: `[maxH] [maxL]`. Length 2 runs through `maxH`. Longer lengths use a lower cap.
 * Defaults: `maxH = 1500`, `maxL = 6`.
 *
 * `through1 [maxH] [length]` follows only the orbit of 1, for that one length, or through
 * `[lastLength]` when a third number is given. There is no tuple enumeration, so `maxH`
 * can sit far above the caps in [hCap]. Default: length 3 through `h = 66100`.
 */
fun main(args: Array<String>) {
    if (args.firstOrNull() == "through1") {
        val maxH = args.getOrElse(1) { "66100" }.toInt()
        val minL = args.getOrElse(2) { "3" }.toInt()
        val maxL = args.getOrElse(3) { minL.toString() }.toInt()
        require(maxH >= 1 && minL >= 2 && maxL >= minL)
        var total = 0
        for (length in minL..maxL) {
            val batch = mutableListOf<Cycle>()
            val ms = measureTimeMillis { batch += searchThroughOne(length, maxH) }
            batch.sortWith(compareBy { it.q })
            total += batch.size
            println("L=$length h<=$maxH through 1: ${batch.size} cycles in ${ms / 1000.0}s")
            for (cycle in batch) println("  $cycle")
        }
        println("total $total")
        return
    }
    val maxH = args.getOrElse(0) { "1500" }.toInt()
    val maxL = args.getOrElse(1) { "6" }.toInt()
    require(maxH >= 1 && maxL >= 2)
    val found = mutableListOf<Cycle>()
    for (length in 2..maxL) {
        val hMax = hCap(length, maxH)
        val batch = mutableListOf<Cycle>()
        val ms = measureTimeMillis { batch += searchLength(length, hMax) }
        batch.sortWith(compareBy({ it.q }, { it.elements.minOrNull() }))
        found += batch
        println("L=$length h<=$hMax: ${batch.size} cycles in ${ms / 1000.0}s")
        for (cycle in batch) println("  $cycle")
    }
    println("total ${found.size}")
}

/** Highest `h` worth scanning at this length. The tuple count grows like `h^{L-2}`. */
fun hCap(length: Int, maxH: Int): Int = when (length) {
    2, 3 -> maxH
    4 -> minOf(maxH, 1600)
    5 -> minOf(maxH, 350)
    6 -> minOf(maxH, 140)
    7 -> minOf(maxH, 80)
    else -> minOf(maxH, 40)
}

data class Cycle(
    val q: BigInteger,
    val h: Int,
    val elements: List<BigInteger>,
) {
    override fun toString(): String {
        val arrows = elements.joinToString(" -> ")
        return "q=$q h=$h $arrows"
    }
}

/**
 * Every `L`-cycle with at most [hMax] halvings.
 * The same cycle is stored once, starting at its least element.
 */
fun searchLength(length: Int, hMax: Int): Set<Cycle> {
    val found = linkedSetOf<Cycle>()
    for (h in 1..hMax) {
        if (h % 500 == 0) System.err.println("L=$length h=$h/${hMax} cycles=${found.size}")
        val q = candidateQ(h, length) ?: continue
        val gap = BigInteger.ONE.shiftLeft(h) - q.pow(length)
        found += cyclesAt(length, h, q, gap)
    }
    return found
}

/**
 * Cycles through 1 of length [length] with at most [hMax] halvings.
 * Each admissible h gives one q, and [trace] is started at 1.
 */
fun searchThroughOne(length: Int, hMax: Int): List<Cycle> {
    val found = mutableListOf<Cycle>()
    for (h in 1..hMax) {
        if (h % 1000 == 0) System.err.println("through 1 L=$length h=$h/$hMax cycles=${found.size}")
        val q = candidateQ(h, length) ?: continue
        val orbit = trace(q, BigInteger.ONE, length) ?: continue
        found += canonical(q, orbit)
    }
    return found
}

/**
 * Odd `q = floor(2^{h/length})` for which a cycle of this length is possible.
 * Even floors and `h` divisible by the length are excluded.
 */
private fun candidateQ(h: Int, length: Int): BigInteger? {
    if (h % length == 0) return null
    val twoH = BigInteger.ONE.shiftLeft(h)
    val q = floorRoot(twoH, length)
    if (q < BigInteger.valueOf(3) || !q.testBit(0)) return null
    if (q.pow(length) >= twoH) return null
    return q
}

private fun cyclesAt(length: Int, h: Int, q: BigInteger, gap: BigInteger): List<Cycle> {
    // 2^h and q^L differ by 1 only for the Catalan pair, which has the other sign.
    if (gap <= BigInteger.ONE) return emptyList()
    val twoMod = Array(h) { BigInteger.ZERO }
    val invTwoMod = Array(h) { BigInteger.ZERO }
    val byResidue = HashMap<BigInteger, MutableList<Int>>(h * 2)
    val inv2 = BigInteger.TWO.modInverse(gap)
    var pow = BigInteger.ONE
    var inv = BigInteger.ONE
    for (k in 0 until h) {
        twoMod[k] = pow
        invTwoMod[k] = inv
        if (k >= 1) byResidue.getOrPut(pow) { mutableListOf() }.add(k)
        pow = pow.shiftLeft(1).mod(gap)
        inv = inv.multiply(inv2).mod(gap)
    }
    val qPower = Array(length) { BigInteger.ONE }
    for (i in 1 until length) qPower[i] = qPower[i - 1].multiply(q).mod(gap)
    val found = mutableListOf<Cycle>()
    val prefix = IntArray(length - 1)
    // Walk k1,...,k_{L-2}. The partial sum of those counts is at most h-2.
    fun walk(pos: Int, sum: Int, acc: BigInteger) {
        if (pos == length - 2) {
            val target = acc.negate().multiply(invTwoMod[sum]).mod(gap)
            val hits = byResidue[target] ?: return
            for (k in hits) {
                if (sum + k >= h) continue
                prefix[length - 2] = k
                val x = element(q, length, prefix, gap) ?: continue
                val orbit = trace(q, x, length) ?: continue
                found += canonical(q, orbit)
            }
            return
        }
        val slotsLeft = length - 3 - pos
        val maxK = h - 2 - sum - slotsLeft
        for (k in 1..maxK) {
            prefix[pos] = k
            val sumK = sum + k
            val term = qPower[length - 2 - pos].multiply(twoMod[sumK]).mod(gap)
            walk(pos + 1, sumK, acc.add(term).mod(gap))
        }
    }
    walk(0, 0, qPower[length - 1])
    return found
}

/** `N / gap` when the stored halving counts make `N` an odd positive multiple of the gap. */
private fun element(q: BigInteger, length: Int, ks: IntArray, gap: BigInteger): BigInteger? {
    var numerator = q.pow(length - 1)
    var sum = 0
    for (j in ks.indices) {
        sum += ks[j]
        val coeff = if (j == ks.lastIndex) BigInteger.ONE else q.pow(length - 2 - j)
        numerator = numerator.add(coeff.shiftLeft(sum))
    }
    val (x, rem) = numerator.divideAndRemainder(gap)
    if (rem.signum() != 0 || x.signum() <= 0 || !x.testBit(0)) return null
    return x
}

/**
 * Follow [start] for exactly [length] steps.
 * Returns the orbit when it first closes there, with every entry odd.
 */
private fun trace(q: BigInteger, start: BigInteger, length: Int): List<BigInteger>? {
    val seen = ArrayList<BigInteger>(length)
    var a = start
    repeat(length) {
        if (a.signum() <= 0 || !a.testBit(0) || a in seen) return null
        seen += a
        val t = q.multiply(a).add(BigInteger.ONE)
        val k = t.lowestSetBit
        if (k <= 0) return null
        a = t.shiftRight(k)
    }
    return if (a == start) seen else null
}

private fun canonical(q: BigInteger, orbit: List<BigInteger>): Cycle {
    val minAt = orbit.indices.minBy { orbit[it] }
    val rotated = orbit.drop(minAt) + orbit.take(minAt)
    val h = rotated.sumOf { step ->
        q.multiply(step).add(BigInteger.ONE).lowestSetBit
    }
    return Cycle(q, h, rotated)
}

/** Greatest integer `r` with `r^degree <= n`. */
private fun floorRoot(n: BigInteger, degree: Int): BigInteger {
    var x = BigInteger.ONE.shiftLeft(n.bitLength() / degree + 1)
    val deg = BigInteger.valueOf(degree.toLong())
    val degM1 = BigInteger.valueOf((degree - 1).toLong())
    while (true) {
        val y = x.multiply(degM1).add(n.divide(x.pow(degree - 1))).divide(deg)
        if (y >= x) break
        x = y
    }
    while (x.pow(degree) > n) x = x.subtract(BigInteger.ONE)
    while (x.add(BigInteger.ONE).pow(degree) <= n) x = x.add(BigInteger.ONE)
    return x
}
