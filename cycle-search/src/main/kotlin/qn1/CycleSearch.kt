package qn1

import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/** Any-element search with no arguments: most halvings. Lengths above 3 also stop at [hCap]. */
private const val DEFAULT_MAX_H = 1500
/** Any-element search with no arguments: longest cycle. */
private const val DEFAULT_MAX_LENGTH = 6

/** Any-element `full` search with no arguments: every requested length uses this many halvings. */
private const val FULL_DEFAULT_MAX_H = 200
/** Any-element `full` search with no arguments: first length. A further number is the last length. */
private const val FULL_DEFAULT_MIN_LENGTH = 6

/** Through-1 search with no arguments: most halvings. */
private const val THROUGH1_DEFAULT_MAX_H = 66100
/** Through-1 search with no arguments: cycle length. A further number is the last length. */
private const val THROUGH1_DEFAULT_MIN_LENGTH = 3

/** `check` with no arguments. Lengths 7 and 8 stop early so the unsplit walk stays cheap. */
private const val CHECK_DEFAULT_MAX_H = 40
private const val CHECK_MIN_LENGTH = 4
private const val CHECK_MAX_LENGTH = 8
private const val CHECK_LENGTH_7_MAX_H = 32
private const val CHECK_LENGTH_8_MAX_H = 24

// Any-element search without `full`: most halvings for that length.
// Lengths 2 and 3 use the requested height. Through-1 does not use these.
private const val H_CAP_LENGTH_4 = 1600
private const val H_CAP_LENGTH_5 = 350
private const val H_CAP_LENGTH_6 = 140
private const val H_CAP_LENGTH_7 = 80
private const val H_CAP_LENGTH_AT_LEAST_8 = 40

private const val MIN_CYCLE_LENGTH = 2

/** Lengths through this one walk the counts. Longer lengths split the numerator. */
private const val WALK_MAX_LENGTH = 3

private const val PARALLEL_PROGRESS_STRIDE = 25
private const val SEQUENTIAL_PROGRESS_STRIDE = 500
private const val THROUGH1_PROGRESS_STRIDE = 1000

/** Packed partial sums use 16 bits each, so one height is at most [MAX_PACKED_H]. */
private const val SUM_BITS = 16
private const val SUM_MASK = (1L shl SUM_BITS) - 1
private const val MAX_PACKED_H = 1 shl SUM_BITS

/** Bytes charged to one stored left state when the thread count is chosen. */
private const val LEFT_STATE_BYTES = 400.0

/** Live left-half tables stay under this, inside the 24g run heap. */
private const val TABLE_BUDGET_BYTES = 12.0 * 1024 * 1024 * 1024

private val MIN_ODD_Q: BigInteger = BigInteger.valueOf(3)

/**
 * Cycles of the odd-to-odd map `C(n) = (q n + 1) / 2^v`, `q` odd and at least 3.
 *
 * An `L`-cycle of odd positive integers uses `h` halvings with `L` not dividing `h`,
 * and `q = floor(2^{h/L})`. That `q` is odd: an even floor leaves no odd candidate
 * inside `q^L < 2^h < (q+1)^L`. The element `x` at the start of the cycle is
 * `N / (2^h - q^L)`, where `N` is the numerator fixed by the first `L-1` halving counts.
 *
 * Two searches. An any-element search allows every odd positive element.
 * A through-1 search follows only the orbit of 1.
 *
 * Any element, `[maxH] [maxL]`. Length 2 runs through `maxH`. Longer lengths stop
 * at [hCap]. Omitted arguments are [DEFAULT_MAX_H] and [DEFAULT_MAX_LENGTH].
 *
 * Any element, `full [maxH] [length] [lastLength]`. Each requested length runs through
 * `maxH`, with no [hCap]. The representative starts at the least element, so each
 * halving count satisfies `2^{k_i} <= q U_{i-1} + 1` with `U_0 = 1` and
 * `U_i = (q U_{i-1} + 1) / 2^{k_i}`.
 * Omitted arguments are [FULL_DEFAULT_MAX_H] and [FULL_DEFAULT_MIN_LENGTH].
 *
 * Through 1, `through1 [maxH] [length] [lastLength]`. One length, or through `lastLength`
 * when a further number is given. There is no tuple enumeration, so `maxH` can sit
 * far above [hCap]. Defaults: [THROUGH1_DEFAULT_MIN_LENGTH] through [THROUGH1_DEFAULT_MAX_H].
 *
 * `check [maxH]` compares the two enumerations. Lengths [CHECK_MIN_LENGTH]–6 run through
 * `maxH` ([CHECK_DEFAULT_MAX_H] when omitted). Length 7 stops at [CHECK_LENGTH_7_MAX_H]
 * and length 8 at [CHECK_LENGTH_8_MAX_H].
 */
fun main(args: Array<String>) {
    when (args.firstOrNull()) {
        "through1" -> {
            val maxH = argInt(args, 1, THROUGH1_DEFAULT_MAX_H)
            val minL = argInt(args, 2, THROUGH1_DEFAULT_MIN_LENGTH)
            val maxL = argInt(args, 3, minL)
            require(maxH >= 1 && minL >= MIN_CYCLE_LENGTH && maxL >= minL)
            runLengths(minL, maxL, " through 1", LengthSearch(maxH, throughOne = true, useCap = false))
            return
        }
        "full" -> {
            val maxH = argInt(args, 1, FULL_DEFAULT_MAX_H)
            val minL = argInt(args, 2, FULL_DEFAULT_MIN_LENGTH)
            val maxL = argInt(args, 3, minL)
            require(maxH >= 1 && minL >= MIN_CYCLE_LENGTH && maxL >= minL)
            runLengths(minL, maxL, "", LengthSearch(maxH, throughOne = false, useCap = false))
            return
        }
        "check" -> {
            checkEnumerations(argInt(args, 1, CHECK_DEFAULT_MAX_H))
            return
        }
    }
    val maxH = argInt(args, 0, DEFAULT_MAX_H)
    val maxL = argInt(args, 1, DEFAULT_MAX_LENGTH)
    require(maxH >= 1 && maxL >= MIN_CYCLE_LENGTH)
    runLengths(MIN_CYCLE_LENGTH, maxL, "", LengthSearch(maxH, throughOne = false, useCap = true))
}

private class LengthSearch(
    private val maxH: Int,
    private val throughOne: Boolean,
    private val useCap: Boolean,
) : (Int) -> Pair<Collection<Cycle>, Int> {
    override fun invoke(length: Int): Pair<Collection<Cycle>, Int> {
        val hMax = if (useCap) hCap(length, maxH) else maxH
        val cycles = if (throughOne) searchThroughOne(length, hMax) else searchLength(length, hMax)
        return cycles to hMax
    }
}

private fun argInt(args: Array<String>, index: Int, default: Int): Int {
    val raw = args.getOrNull(index) ?: return default
    return raw.toInt()
}

private fun runLengths(
    minL: Int,
    maxL: Int,
    label: String,
    search: (Int) -> Pair<Collection<Cycle>, Int>,
) {
    var total = 0
    for (length in minL..maxL) {
        val batch = mutableListOf<Cycle>()
        var hMax = 0
        val ms = measureTimeMillis {
            val (cycles, cap) = search(length)
            hMax = cap
            batch += cycles
        }
        batch.sortWith(compareBy({ it.q }, { it.elements.minOrNull() }))
        total += batch.size
        println("L=$length h<=$hMax$label: ${batch.size} cycles in ${ms / 1000.0}s")
        for (cycle in batch) println("  $cycle")
    }
    println("total $total")
}

/**
 * Most halvings for [length] in an any-element search that was not started with `full`.
 * Lengths 2 and 3 return [maxH]. Through-1 does not call this.
 */
fun hCap(length: Int, maxH: Int): Int = when (length) {
    2, 3 -> maxH
    4 -> minOf(maxH, H_CAP_LENGTH_4)
    5 -> minOf(maxH, H_CAP_LENGTH_5)
    6 -> minOf(maxH, H_CAP_LENGTH_6)
    7 -> minOf(maxH, H_CAP_LENGTH_7)
    else -> minOf(maxH, H_CAP_LENGTH_AT_LEAST_8)
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
    if (length <= WALK_MAX_LENGTH) return searchLengthSequential(length, hMax)
    val threads = poolSize(length, hMax)
    val found = linkedSetOf<Cycle>()
    val done = AtomicInteger()
    val hits = AtomicInteger()
    val pool = Executors.newFixedThreadPool(threads)
    System.err.println("L=$length h<=$hMax on $threads threads")
    try {
        val futures = (1..hMax).map { h ->
            pool.submit(HeightSearch(length, h, hMax, done, hits))
        }
        for (future in futures) found += future.get()
    } finally {
        pool.shutdownNow()
    }
    return found
}

/** One admissible height. Several of these run at once for length above [WALK_MAX_LENGTH]. */
private class HeightSearch(
    private val length: Int,
    private val h: Int,
    private val hMax: Int,
    private val done: AtomicInteger,
    private val hits: AtomicInteger,
) : Callable<List<Cycle>> {
    override fun call(): List<Cycle> {
        val adm = admissible(h, length)
        val batch = if (adm == null) emptyList() else cyclesAt(length, h, adm.q, adm.gap)
        val finished = done.incrementAndGet()
        if (batch.isNotEmpty()) {
            hits.addAndGet(batch.size)
            synchronized(System.err) {
                for (cycle in batch) System.err.println("hit $cycle")
            }
        }
        if (h % PARALLEL_PROGRESS_STRIDE == 0 || h == hMax) {
            synchronized(System.err) {
                System.err.println(
                    "L=$length h=$h/$hMax done=$finished/$hMax cycles=${hits.get()}",
                )
            }
        }
        return batch
    }
}

private fun searchLengthSequential(length: Int, hMax: Int): Set<Cycle> {
    val found = linkedSetOf<Cycle>()
    for (h in 1..hMax) {
        if (h % SEQUENTIAL_PROGRESS_STRIDE == 0) System.err.println("L=$length h=$h/$hMax cycles=${found.size}")
        val adm = admissible(h, length) ?: continue
        found += cyclesAt(length, h, adm.q, adm.gap)
    }
    return found
}

/**
 * Several left-half tables are live at once. Each one stores the partial sums
 * that can still keep `x0` least. Stay inside the 24g run heap.
 */
private fun poolSize(length: Int, hMax: Int): Int {
    val left = (length - 1) / 2
    // q + 1 < 2^{logQ}, so the j-th count has fewer than j * logQ choices.
    val logQ = (hMax + length - 1) / length + 1
    var states = 1.0
    for (j in 1..left) states *= j * logQ
    val perHeight = states * LEFT_STATE_BYTES
    val byMemory = if (!perHeight.isFinite()) 1 else (TABLE_BUDGET_BYTES / perHeight).toInt().coerceAtLeast(1)
    return minOf(Runtime.getRuntime().availableProcessors(), byMemory)
}

/**
 * Cycles through 1 of length [length] with at most [hMax] halvings.
 * Each admissible h gives one q, and [trace] is started at 1.
 */
fun searchThroughOne(length: Int, hMax: Int): List<Cycle> {
    val found = mutableListOf<Cycle>()
    for (h in 1..hMax) {
        if (h % THROUGH1_PROGRESS_STRIDE == 0) {
            System.err.println("through 1 L=$length h=$h/$hMax cycles=${found.size}")
        }
        val adm = admissible(h, length) ?: continue
        val orbit = trace(adm.q, BigInteger.ONE, length) ?: continue
        found += canonical(adm.q, orbit)
    }
    return found
}

/**
 * Odd `q = floor(2^{h/length})` and gap `2^h - q^length`.
 * Even floors and `h` divisible by the length are excluded.
 */
private class Admissible(val q: BigInteger, val gap: BigInteger)

private fun admissible(h: Int, length: Int): Admissible? {
    if (h % length == 0) return null
    val twoH = BigInteger.ONE.shiftLeft(h)
    val q = floorRoot(twoH, length)
    if (q < MIN_ODD_Q || !q.testBit(0)) return null
    val qPow = q.pow(length)
    if (qPow >= twoH) return null
    return Admissible(q, twoH.subtract(qPow))
}

private fun cyclesAt(length: Int, h: Int, q: BigInteger, gap: BigInteger): List<Cycle> {
    // 2^h and q^L differ by 1 only for the Catalan pair, which has the other sign.
    if (gap <= BigInteger.ONE) return emptyList()
    val found = mutableListOf<Cycle>()
    val accept = CycleRecorder(found, length, h, q, gap)
    if (length <= WALK_MAX_LENGTH) forEachWalk(length, h, q, gap, cap = true, accept)
    else forEachSplit(length, h, q, gap, cap = true, accept)
    return found
}

/**
 * Keeps a halving vector only when `x0` is least, then traces it.
 * The split cap is a uniform bound, so this check is what makes the representative minimal.
 */
private class CycleRecorder(
    private val found: MutableList<Cycle>,
    private val length: Int,
    private val h: Int,
    private val q: BigInteger,
    private val gap: BigInteger,
) : (IntArray) -> Unit {
    override fun invoke(ks: IntArray) {
        if (!respectsMinimum(q, ks, h - ks.sum())) return
        val x = element(q, length, ks, gap) ?: return
        val orbit = trace(q, x, length) ?: return
        found += canonical(q, orbit)
    }
}

/**
 * Greatest `k` with `2^{sum+k} <= q * envelope + 2^{sum}`.
 * [envelope] is the numerator of the running upper bound `U` for `x/x0`, over `2^{sum}`.
 */
private fun maxHalvings(q: BigInteger, envelope: BigInteger, sum: Int): Int {
    val limit = q.multiply(envelope).add(BigInteger.ONE.shiftLeft(sum))
    return limit.bitLength() - 1 - sum
}

/**
 * True when each halving count keeps the least element at `x0`.
 * [prefix] holds `k1..k_{L-1}` and [kLast] is the return count.
 */
private fun respectsMinimum(q: BigInteger, prefix: IntArray, kLast: Int): Boolean {
    var envelope = BigInteger.ONE
    var sum = 0
    for (k in prefix) {
        if (k > maxHalvings(q, envelope, sum)) return false
        envelope = q.multiply(envelope).add(BigInteger.ONE.shiftLeft(sum))
        sum += k
    }
    return kLast <= maxHalvings(q, envelope, sum)
}

/**
 * `ks` is valid only for the duration of [block].
 * Walks `k1,...,k_{L-2}` and solves the next count from `N ≡ 0` modulo [gap].
 * With [cap], `x0` is the least element, so [maxHalvings] limits every count.
 */
private fun forEachWalk(
    length: Int,
    h: Int,
    q: BigInteger,
    gap: BigInteger,
    cap: Boolean,
    block: (IntArray) -> Unit,
) {
    WalkSearch(length, h, q, gap, cap, block).run()
}

private class WalkSearch(
    private val length: Int,
    private val h: Int,
    private val q: BigInteger,
    private val gap: BigInteger,
    private val cap: Boolean,
    private val block: (IntArray) -> Unit,
) {
    private val twoMod = Array(h) { BigInteger.ZERO }
    private val invTwoMod = Array(h) { BigInteger.ZERO }
    private val byResidue = HashMap<BigInteger, MutableList<Int>>(h * 2)
    private val qPower = Array(length) { BigInteger.ONE }
    private val prefix = IntArray(length - 1)

    fun run() {
        val inv2 = BigInteger.TWO.modInverse(gap)
        var pow = BigInteger.ONE
        var inv = BigInteger.ONE
        for (k in 0 until h) {
            twoMod[k] = pow
            invTwoMod[k] = inv
            if (k >= 1) addResidue(pow, k)
            pow = pow.shiftLeft(1).mod(gap)
            inv = inv.multiply(inv2).mod(gap)
        }
        for (i in 1 until length) qPower[i] = qPower[i - 1].multiply(q).mod(gap)
        // Partial sum of the free counts stays at most h-2, leaving two positive counts.
        // `envelope / 2^sum` is the running upper bound for the current element over x0.
        walk(0, 0, qPower[length - 1], BigInteger.ONE)
    }

    private fun addResidue(pow: BigInteger, k: Int) {
        val hits = byResidue[pow]
        if (hits == null) {
            val created = ArrayList<Int>(1)
            created.add(k)
            byResidue[pow] = created
        } else {
            hits.add(k)
        }
    }

    private fun walk(pos: Int, sum: Int, acc: BigInteger, envelope: BigInteger) {
        if (pos == length - 2) {
            val target = acc.negate().multiply(invTwoMod[sum]).mod(gap)
            val hits = byResidue[target] ?: return
            val solvedCap = if (cap) maxHalvings(q, envelope, sum) else h
            for (k in hits) {
                if (sum + k >= h || k > solvedCap) continue
                prefix[length - 2] = k
                if (cap && !respectsMinimum(q, prefix, h - sum - k)) continue
                block(prefix)
            }
            return
        }
        val slotsLeft = length - 3 - pos
        val room = h - 2 - sum - slotsLeft
        val maxK = if (cap) minOf(room, maxHalvings(q, envelope, sum)) else room
        val nextEnvelope = q.multiply(envelope).add(BigInteger.ONE.shiftLeft(sum))
        for (k in 1..maxK) {
            prefix[pos] = k
            val sumK = sum + k
            val term = qPower[length - 2 - pos].multiply(twoMod[sumK]).mod(gap)
            walk(pos + 1, sumK, addMod(acc, term, gap), nextEnvelope)
        }
    }
}

/**
 * After `j` steps at sum `s`, the next count is at most
 * `floor(log2(q (q+1)^j + 2^s)) - s` whenever `x0` is least.
 * On that path the envelope numerator is at most `(q+1)^j`, with equality for `j < 2`.
 */
private fun looseCaps(length: Int, h: Int, q: BigInteger): Array<IntArray> {
    val qPlus = q.add(BigInteger.ONE)
    val caps = Array(length) { IntArray(h) }
    var bound = BigInteger.ONE
    for (j in 0 until length) {
        val scaled = q.multiply(bound)
        var pow2 = BigInteger.ONE
        for (s in 0 until h) {
            // Same value as maxHalvings(q, bound, s): floor(log2(q*bound + 2^s)) - s.
            caps[j][s] = (scaled.add(pow2).bitLength() - 1 - s).coerceAtLeast(0)
            pow2 = pow2.shiftLeft(1)
        }
        bound = bound.multiply(qPlus)
    }
    return caps
}

/**
 * `ks` is valid only for the duration of [block].
 * Partial sums `0 = s0 < s1 < ... < s_{L-1} < h` split after the smaller half.
 * Left residues are activated in order of `s_left`, and each right continuation
 * starts strictly later, so the two halves do not interleave.
 *
 * With [cap], [looseCaps] limits every count. The right half is shared by every
 * left state, so those limits depend on the step index and the sum so far.
 * The first right count still depends on the left sum and is checked per match.
 */
private fun forEachSplit(
    length: Int,
    h: Int,
    q: BigInteger,
    gap: BigInteger,
    cap: Boolean,
    block: (IntArray) -> Unit,
) {
    SplitSearch(length, h, q, gap, cap, block).run()
}

private class SplitSearch(
    private val length: Int,
    private val h: Int,
    private val q: BigInteger,
    private val gap: BigInteger,
    private val cap: Boolean,
    private val block: (IntArray) -> Unit,
) {
    private val free = length - 1
    private val leftCount = free / 2
    private val rightCount = free - leftCount
    private val capAt: Array<IntArray>?
    private val term: Array<Array<BigInteger>>
    private val coeff0: BigInteger
    private val byEnd: Array<ArrayList<Pair<BigInteger, Long>>>
    private val leftSums = IntArray(leftCount)
    private val rightSums = IntArray(rightCount)
    private val partial = IntArray(free)
    private val ks = IntArray(free)
    private lateinit var active: HashMap<BigInteger, MutableList<Long>>

    init {
        require(length > WALK_MAX_LENGTH)
        require(h <= MAX_PACKED_H) { "halving count $h does not fit the packed partial sums" }
        require(leftCount * SUM_BITS <= 64) { "length $length has too many left partial sums to pack" }
        capAt = if (cap) looseCaps(length, h, q) else null
        val coeff = Array(length) { BigInteger.ONE }
        var powQ = BigInteger.ONE
        for (j in length - 2 downTo 0) {
            powQ = powQ.multiply(q).mod(gap)
            coeff[j] = powQ
        }
        coeff0 = coeff[0]
        val pow2 = Array(h) { BigInteger.ONE }
        for (s in 1 until h) pow2[s] = addMod(pow2[s - 1], pow2[s - 1], gap)
        term = Array(length) { j ->
            if (j == 0) emptyArray() else Array(h) { s ->
                if (s == 0) BigInteger.ZERO else coeff[j].multiply(pow2[s]).mod(gap)
            }
        }
        byEnd = Array(h) { ArrayList() }
    }

    fun run() {
        genLeft(0, 0, coeff0)
        var maxStart = if (capAt == null) h - 1 else 0
        val caps = capAt
        if (caps != null) {
            val firstRight = caps[leftCount]
            for (s in 0 until h) {
                if (byEnd[s].isEmpty()) continue
                val reach = s + firstRight[s]
                if (reach > maxStart) maxStart = reach
            }
        }
        var states = 0
        for (bucket in byEnd) states += bucket.size
        active = HashMap(states * 2 + 16)
        for (start in 1..maxStart) {
            if (start >= h) break
            val ended = byEnd[start - 1]
            for ((neg, packed) in ended) addActive(neg, packed)
            ended.clear()
            if (start + rightCount - 1 > h - 1) continue
            rightSums[0] = start
            genRight(1, start, term[leftCount + 1][start])
        }
    }

    private fun cappedS(stepsDone: Int, sum: Int, maxS: Int): Int {
        val table = capAt ?: return maxS
        return minOf(maxS, sum + table[stepsDone][sum])
    }

    private fun genLeft(pos: Int, prev: Int, acc: BigInteger) {
        if (pos == leftCount) {
            val neg = if (acc.signum() == 0) BigInteger.ZERO else gap.subtract(acc)
            byEnd[leftSums[leftCount - 1]].add(neg to packSums(leftSums, leftCount))
            return
        }
        val remainingAfter = (leftCount - 1 - pos) + rightCount
        val maxS = h - 1 - remainingAfter
        for (s in prev + 1..cappedS(pos, prev, maxS)) {
            leftSums[pos] = s
            genLeft(pos + 1, s, addMod(acc, term[pos + 1][s], gap))
        }
    }

    private fun genRight(pos: Int, prev: Int, acc: BigInteger) {
        if (pos == rightCount) {
            emit(acc)
            return
        }
        val remainingAfter = rightCount - 1 - pos
        val maxS = h - 1 - remainingAfter
        for (s in prev + 1..cappedS(leftCount + pos, prev, maxS)) {
            rightSums[pos] = s
            genRight(pos + 1, s, addMod(acc, term[leftCount + 1 + pos][s], gap))
        }
    }

    private fun emit(acc: BigInteger) {
        val matches = active[acc] ?: return
        val last = rightSums[rightCount - 1]
        val caps = capAt
        if (caps != null && h - last > caps[free][last]) return
        val start = rightSums[0]
        val firstRight = caps?.get(leftCount)
        for (i in 0 until rightCount) partial[leftCount + i] = rightSums[i]
        for (packed in matches) {
            val sLeft = ((packed ushr ((leftCount - 1) * SUM_BITS)) and SUM_MASK).toInt()
            if (firstRight != null && start - sLeft > firstRight[sLeft]) continue
            unpackSums(packed, leftCount, partial, 0)
            var prev = 0
            for (i in partial.indices) {
                ks[i] = partial[i] - prev
                prev = partial[i]
            }
            block(ks)
        }
    }

    private fun addActive(neg: BigInteger, packed: Long) {
        val bucket = active[neg]
        if (bucket == null) {
            val created = ArrayList<Long>(1)
            created.add(packed)
            active[neg] = created
        } else {
            bucket.add(packed)
        }
    }
}

private fun packSums(sums: IntArray, count: Int): Long {
    var packed = 0L
    for (i in 0 until count) packed = packed or (sums[i].toLong() shl (i * SUM_BITS))
    return packed
}

private fun unpackSums(packed: Long, count: Int, dest: IntArray, offset: Int) {
    for (i in 0 until count) dest[offset + i] = ((packed ushr (i * SUM_BITS)) and SUM_MASK).toInt()
}

/** Both arguments lie in `0..gap-1`, so the sum needs at most one subtraction. */
private fun addMod(a: BigInteger, b: BigInteger, gap: BigInteger): BigInteger {
    val sum = a.add(b)
    return if (sum.compareTo(gap) >= 0) sum.subtract(gap) else sum
}

/**
 * Halving vectors from one enumeration.
 * [minimal] is the subset that keeps `x0` least. With [minimalOnly], [keys] is that subset.
 */
private class VectorKeys(
    private val q: BigInteger,
    private val h: Int,
    private val minimalOnly: Boolean,
) : (IntArray) -> Unit {
    val keys = linkedSetOf<String>()
    val minimal = linkedSetOf<String>()

    override fun invoke(ks: IntArray) {
        val keep = respectsMinimum(q, ks, h - ks.sum())
        if (minimalOnly && !keep) return
        val key = ks.joinToString(",")
        keys.add(key)
        if (keep) minimal.add(key)
    }
}

/**
 * The walk and the split must accept the same halving vectors.
 * With the least-element cap, both must accept the vectors that keep `x0` least.
 * A mismatch means a later search would miss a cycle or invent one before [trace].
 */
private fun checkEnumerations(maxH: Int) {
    require(maxH >= 1)
    for (length in CHECK_MIN_LENGTH..CHECK_MAX_LENGTH) {
        val limit = when (length) {
            7 -> minOf(maxH, CHECK_LENGTH_7_MAX_H)
            8 -> minOf(maxH, CHECK_LENGTH_8_MAX_H)
            else -> maxH
        }
        var count = 0
        for (h in 1..limit) {
            val adm = admissible(h, length) ?: continue
            val walk = VectorKeys(adm.q, h, minimalOnly = false)
            val split = VectorKeys(adm.q, h, minimalOnly = false)
            val capped = VectorKeys(adm.q, h, minimalOnly = false)
            val fromSplit = VectorKeys(adm.q, h, minimalOnly = true)
            forEachWalk(length, h, adm.q, adm.gap, cap = false, walk)
            forEachWalk(length, h, adm.q, adm.gap, cap = true, capped)
            forEachSplit(length, h, adm.q, adm.gap, cap = false, split)
            forEachSplit(length, h, adm.q, adm.gap, cap = true, fromSplit)
            if (walk.keys != split.keys || capped.keys != walk.minimal || capped.keys != fromSplit.keys) {
                error(
                    "mismatch L=$length h=$h walkSplit=${walk.keys == split.keys} " +
                        "cappedWalk=${capped.keys == walk.minimal} splitCap=${capped.keys == fromSplit.keys} " +
                        "missed=${(walk.minimal - fromSplit.keys).take(8)} " +
                        "extra=${(fromSplit.keys - walk.minimal).take(8)}",
                )
            }
            count += capped.keys.size
        }
        println("L=$length h<=$limit match ($count vectors)")
    }
    println("check ok")
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
