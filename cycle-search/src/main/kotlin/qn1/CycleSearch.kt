package qn1

import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

/** All starting values, no arguments: most halvings. One length. */
internal const val DEFAULT_MAX_H = 1500
/** All starting values, no arguments: that length. */
internal const val DEFAULT_LENGTH = 6

/** Through 1, no arguments: most halvings. One length. */
internal const val THROUGH1_DEFAULT_MAX_H = 66100
/** Through 1, no arguments: that length. */
internal const val THROUGH1_DEFAULT_LENGTH = 3

/** Fixed q, no arguments: that odd multiplier. */
internal const val Q_DEFAULT = 5
/** Fixed q, no arguments: longest cycle. Lengths run from 2 through this one. */
internal const val Q_DEFAULT_MAX_LENGTH = 20

/** Length 1 is C(x)=x, so x(2^h-q)=1: only x=1 and q=2^h-1. */
internal const val MIN_CYCLE_LENGTH = 2

/** Lengths through this one walk the counts. Longer lengths split the numerator. */
private const val WALK_MAX_LENGTH = 3

/** A walked half this deep is the fixed-q cost. The least-element bound is cheaper there. */
private const val DEEP_RIGHT_COUNT = 7

private const val PARALLEL_PROGRESS_STRIDE = 25
private const val SEQUENTIAL_PROGRESS_STRIDE = 500
private const val THROUGH1_PROGRESS_STRIDE = 1000

/** A partial sum uses at most this many bits, so one value of h is at most [MAX_PACKED_H]. */
private const val SUM_BIT_LIMIT = 16
private const val MAX_PACKED_H = 1 shl SUM_BIT_LIMIT

/**
 * Bytes charged to one stored left state when the thread count is chosen.
 * One state is a residue hash and a packed partial sum, plus its open-addressed slot.
 */
private const val LEFT_STATE_BYTES = 96.0

/** Live left-half tables stay under this, inside the 24g run heap. */
private const val TABLE_BUDGET_BYTES = 12.0 * 1024 * 1024 * 1024

/**
 * Odd starts followed when the left partial sums do not fit in one long, or the packed table is past [PACKED_STATE_LIMIT].
 * The least element of a cycle is at most the numerator bound; above this many starts the trace is refused.
 */
private val TRACE_LEAST_LIMIT: BigInteger = BigInteger.valueOf(1_000_000)

/**
 * Packed left tables no larger than this stay on the split search.
 * A larger estimate, or a walked half of [DEEP_RIGHT_COUNT] sums, uses the least-element bound
 * when that bound is at most [TRACE_LEAST_LIMIT].
 */
private const val PACKED_STATE_LIMIT = 50_000_000.0

private val MIN_ODD_Q: BigInteger = BigInteger.valueOf(3)

/**
 * Cycles of the odd-to-odd map `C(n) = (q n + 1) / 2^v`, `q` odd and at least 3.
 *
 * An `L`-cycle of odd positive integers uses `h` halvings with `L` not dividing `h`,
 * and `q = floor(2^{h/L})`. That `q` is odd: an even floor leaves no odd candidate
 * inside `q^L < 2^h < (q+1)^L`. The element `x` at the start of the cycle is
 * `N / (2^h - q^L)`, where `N` is the numerator fixed by the first `L-1` halving counts.
 *
 * Named options, as parsed by [parseCli].
 *
 * `--length` and `--max-h` search one length over every odd element.
 * The representative starts at the least element, so each halving count satisfies
 * `2^{k_i} <= q U_{i-1} + 1` with `U_0 = 1` and `U_i = (q U_{i-1} + 1) / 2^{k_i}`.
 * Omitted values are [DEFAULT_LENGTH] and [DEFAULT_MAX_H].
 *
 * `--through-1` follows only the orbit of 1. There is no tuple enumeration, so `--max-h`
 * can sit far above an all-starting-values search. Omitted values are [THROUGH1_DEFAULT_LENGTH]
 * and [THROUGH1_DEFAULT_MAX_H].
 *
 * `--q`, `--min-length`, and `--max-length` fix the odd multiplier and the lengths to search.
 * The only values of `h` are those with this `q = floor(2^{h/L})`. When the left partial sums
 * do not fit in one long, or the packed table is past [PACKED_STATE_LIMIT], or the walked half
 * has [DEEP_RIGHT_COUNT] sums, and the least element is at most [TRACE_LEAST_LIMIT], every odd
 * start up to that bound is followed.
 * Omitted values are [Q_DEFAULT], [MIN_CYCLE_LENGTH], and [Q_DEFAULT_MAX_LENGTH].
 *
 * `--bounds q3star`, `--bounds q30`, `--bounds q2star`, and `--bounds q2` each print one Paper II table.
 * `--x` selects one odd element; omitted, the odds from 1 through 35.
 *
 * `--tables through1`, `--tables any`, and `--tables q` rerun the Paper I computation tables.
 * `--length` or `--q` selects one published row. `--max-h` or `--max-length` overrides that row's limit.
 *
 * `--digits cbrt2` scans the first [DIGIT_PLACES] binary places of 2^{1/3} and of 2^{2/3}.
 * `--digits sqrt2` scans the first [SQRT2_PLACES] binary places of √2.
 * `--places` scans fewer. Both scans begin at m = 1.
 * The cube-root scan reports the longest alternating run, and the square-root scan reports the longest zero run.
 */
fun main(args: Array<String>) {
    val cli = try {
        parseCli(args)
    } catch (e: IllegalArgumentException) {
        if (!e.message.isNullOrEmpty()) System.err.println(e.message)
        usage()
    }
    when (cli.mode) {
        SearchMode.FIXED_Q -> searchFixedQ(cli.q!!, cli.maxLength!!, cli.minLength!!)
        SearchMode.BOUNDS -> runBounds(cli.bound!!, cli.x)
        SearchMode.TABLES -> runTables(cli.table!!, cli.length, cli.maxH, cli.q, cli.maxLength)
        SearchMode.DIGITS -> when (cli.digits) {
            "cbrt2" -> runDigits(cli.places!!)
            "sqrt2" -> runSqrt2(cli.places!!)
            else -> error("digits ${cli.digits}")
        }
        SearchMode.ANY, SearchMode.THROUGH_1 -> runLengthSearch(cli)
    }
}

private fun runLengthSearch(cli: Cli) {
    val throughOne = cli.mode == SearchMode.THROUGH_1
    val length = cli.length!!
    val maxH = cli.maxH!!
    val batch = mutableListOf<Cycle>()
    val ms = measureTimeMillis {
        val cycles = if (throughOne) searchThroughOne(length, maxH) else searchLength(length, maxH)
        batch += cycles
    }
    batch.sortWith(compareBy({ it.q }, { it.elements.minOrNull() }))
    val label = if (throughOne) " through 1" else ""
    println("L=$length h<=$maxH$label: ${batch.size} cycles in ${ms / 1000.0}s")
    for (cycle in batch) println("  $cycle")
}

internal fun usage(): Nothing {
    System.err.println("usage: --length N --max-h N")
    System.err.println("       --through-1 --length N --max-h N")
    System.err.println("       --q N [--min-length N] --max-length N")
    System.err.println("       --bounds q3star|q30|q2star|q2 [--x N]")
    System.err.println("       --tables through1|any [--length N] [--max-h N]")
    System.err.println("       --tables q [--q N] [--max-length N]")
    System.err.println("       --digits cbrt2|sqrt2 [--places N]")
    exitProcess(1)
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
    if (length == 3) return searchLengthSequential(length, hMax)
    val threads = poolSize(length, hMax)
    val found = linkedSetOf<Cycle>()
    val done = AtomicInteger()
    val hits = AtomicInteger()
    val pool = Executors.newFixedThreadPool(threads)
    System.err.println("L=$length h<=$hMax on $threads threads")
    try {
        val futures = (1..hMax).map { h ->
            pool.submit(SearchAtH(length, h, hMax, done, hits))
        }
        for (future in futures) found += future.get()
    } finally {
        pool.shutdownNow()
    }
    return found
}

/** One admissible value of h, the total number of halvings. Several of these run at once. Length 3 stays on one thread. */
private class SearchAtH(
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
    val perH = estimatedLeftStates(length, hMax, storedHalfCount(length, hMax)) * LEFT_STATE_BYTES
    val byMemory = if (!perH.isFinite()) 1 else (TABLE_BUDGET_BYTES / perH).toInt().coerceAtLeast(1)
    return minOf(Runtime.getRuntime().availableProcessors(), byMemory)
}

/**
 * Upper bound on stored left states at this h.
 * q + 1 < 2^logQ, so the j-th count has fewer than j * logQ choices.
 */
private fun estimatedLeftStates(length: Int, h: Int, left: Int): Double {
    val logQ = (h + length - 1) / length + 1
    var states = 1.0
    for (j in 1..left) states *= j.toDouble() * logQ
    return states
}

/**
 * How many partial sums are stored.
 * The extra sum is stored when the other half would otherwise be walked at depth 3 or more
 * and the stored table fits in the heap. A walked half of two sums is already shallow.
 */
private fun storedHalfCount(length: Int, h: Int): Int {
    val free = length - 1
    val smaller = free / 2
    val larger = free - smaller
    if (larger < 3) return smaller
    if (larger * bitsForSums(h) > 64) return smaller
    val bytes = estimatedLeftStates(length, h, larger) * LEFT_STATE_BYTES
    if (bytes * 2 > TABLE_BUDGET_BYTES) return smaller
    return larger
}

/**
 * Every cycle for one odd [q], of length [minLength] through [maxLength].
 * A length whose power window contains no integer h has no cycle.
 */
fun searchFixedQ(q: Int, maxLength: Int, minLength: Int = MIN_CYCLE_LENGTH): List<Cycle> {
    require(q >= MIN_ODD_Q.intValueExact() && q % 2 == 1)
    require(minLength >= MIN_CYCLE_LENGTH)
    require(maxLength >= minLength)
    val qBig = BigInteger.valueOf(q.toLong())
    val found = mutableListOf<Cycle>()
    for (length in minLength..maxLength) {
        val hs = hForQ(qBig, length)
        if (hs.isEmpty()) {
            println("q=$q L=$length: no h")
            continue
        }
        val batch = mutableListOf<Cycle>()
        val ms = measureTimeMillis {
            for (h in hs) {
                val adm = admissible(h, length) ?: continue
                if (adm.q != qBig) continue
                batch += cyclesAt(length, h, adm.q, adm.gap)
            }
        }
        batch.sortWith(compareBy({ it.h }, { it.elements.minOrNull() }))
        val hText = if (hs.first == hs.last) "${hs.first}" else "${hs.first}..${hs.last}"
        println("q=$q L=$length h=$hText: ${batch.size} cycles in ${ms / 1000.0}s")
        for (cycle in batch) println("  $cycle")
        found += batch
    }
    return found
}

/**
 * Integers `h` with `q^L < 2^h < (q+1)^L`.
 * Empty when that q is not `floor(2^{h/L})` for any h.
 */
internal fun hForQ(q: BigInteger, length: Int): IntRange {
    val hMin = q.pow(length).bitLength()
    val upper = q.add(BigInteger.ONE).pow(length)
    val hMax = if (upper.bitCount() == 1) upper.bitLength() - 2 else upper.bitLength() - 1
    if (hMin > hMax) return 1..0
    return hMin..hMax
}

/**
 * Cycles through 1 of length [length] with at most [hMax] halvings.
 * Each admissible h gives one q, and [trace] is started at 1.
 * Those h are independent, so they run together.
 */
fun searchThroughOne(length: Int, hMax: Int): List<Cycle> {
    if (hMax < 1) return emptyList()
    val threads = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
    if (threads == 1 || hMax < threads * 4) {
        return throughOneRange(length, hMax, 1, 1, report = false)
    }
    val done = AtomicInteger()
    val hits = AtomicInteger()
    val pool = Executors.newFixedThreadPool(threads)
    System.err.println("through 1 L=$length h<=$hMax on $threads threads")
    try {
        val futures = (0 until threads).map { worker ->
            pool.submit(Callable {
                throughOneRange(length, hMax, worker + 1, threads, report = true, done, hits)
            })
        }
        val found = mutableListOf<Cycle>()
        for (future in futures) found += future.get()
        return found.sortedBy { it.h }
    } finally {
        pool.shutdownNow()
    }
}

private fun throughOneRange(
    length: Int,
    hMax: Int,
    first: Int,
    step: Int,
    report: Boolean,
    done: AtomicInteger? = null,
    hits: AtomicInteger? = null,
): List<Cycle> {
    val found = mutableListOf<Cycle>()
    var h = first
    while (h <= hMax) {
        val cycle = cycleThroughOne(length, h)
        if (cycle != null) {
            found += cycle
            hits?.incrementAndGet()
        }
        val finished = done?.incrementAndGet() ?: h
        if (report && (finished % THROUGH1_PROGRESS_STRIDE == 0 || finished == hMax)) {
            System.err.println("through 1 L=$length h=$finished/$hMax cycles=${hits?.get() ?: found.size}")
        }
        h += step
    }
    return found
}

private fun cycleThroughOne(length: Int, h: Int): Cycle? {
    val adm = admissible(h, length) ?: return null
    val orbit = trace(adm.q, BigInteger.ONE, length) ?: return null
    return canonical(adm.q, orbit)
}

/**
 * Odd `q = floor(2^{h/length})` and gap `2^h - q^length`.
 * Even floors and `h` divisible by the length are excluded.
 */
internal class Admissible(val q: BigInteger, val gap: BigInteger)

internal fun admissible(h: Int, length: Int): Admissible? {
    if (h % length == 0) return null
    val twoH = BigInteger.ONE.shiftLeft(h)
    val (q, qPow) = floorRootAndPower(twoH, length)
    if (q < MIN_ODD_Q || !q.testBit(0)) return null
    if (qPow >= twoH) return null
    return Admissible(q, twoH.subtract(qPow))
}

private fun cyclesAt(length: Int, h: Int, q: BigInteger, gap: BigInteger): List<Cycle> {
    // 2^h and q^L differ by 1 only for the Catalan pair, which has the other sign.
    if (gap <= BigInteger.ONE) return emptyList()
    if (length > WALK_MAX_LENGTH) {
        val packed = sumsPackIntoOneLong(length, h)
        if (!packed) return traceByLeastElement(length, h, q, gap)
        val stored = storedHalfCount(length, h)
        val states = estimatedLeftStates(length, h, stored)
        val right = length - 1 - stored
        if (states > PACKED_STATE_LIMIT || right >= DEEP_RIGHT_COUNT) {
            val least = maxLeastElement(length, h, q, gap)
            if (least <= TRACE_LEAST_LIMIT) return followLeastElement(length, h, q, least)
        }
    }
    val found = mutableListOf<Cycle>()
    val accept = CycleRecorder(found, length, h, q, gap)
    if (length == 2) forEachLength2(h, q, gap, accept)
    else if (length <= WALK_MAX_LENGTH) forEachWalk(length, h, q, gap, accept)
    else forEachSplit(length, h, q, gap, accept)
    return found
}

/** True when [SplitSearch] can store every left partial sum in one long. */
private fun sumsPackIntoOneLong(length: Int, h: Int): Boolean {
    if (h > MAX_PACKED_H) return false
    return storedHalfCount(length, h) * bitsForSums(h) <= 64
}

/**
 * Every cycle of this length, `q`, and `h`.
 * The least element is at most [maxLeastElement], so following each odd start up to that bound finds the cycle.
 */
internal fun traceByLeastElement(
    length: Int,
    h: Int,
    q: BigInteger,
    gap: BigInteger,
): List<Cycle> {
    val least = maxLeastElement(length, h, q, gap)
    require(least <= TRACE_LEAST_LIMIT) {
        "least element bound $least at length $length h=$h is above $TRACE_LEAST_LIMIT"
    }
    return followLeastElement(length, h, q, least)
}

/** Every cycle whose least element is at most [least]. A non-positive bound means there is no cycle. */
private fun followLeastElement(length: Int, h: Int, q: BigInteger, least: BigInteger): List<Cycle> {
    if (least.signum() <= 0) return emptyList()
    val found = mutableListOf<Cycle>()
    var x = BigInteger.ONE
    val two = BigInteger.TWO
    while (x <= least) {
        val orbit = trace(q, x, length)
        if (orbit != null && orbit.minOrNull() == x) {
            val cycle = canonical(q, orbit)
            if (cycle.h == h) found += cycle
        }
        x = x.add(two)
    }
    return found
}

/**
 * Upper bound on the least element of a cycle of this length, `q`, and `h`.
 * With that element first, [looseCaps] limits every halving count, and the numerator is at most the largest value under those caps.
 */
internal fun maxLeastElement(length: Int, h: Int, q: BigInteger, gap: BigInteger): BigInteger {
    val caps = looseCaps(length, h, q)
    val free = length - 1
    var layer = arrayOfNulls<BigInteger>(h)
    layer[0] = q.pow(free)
    for (pos in 0 until free) {
        val coeff = if (pos == free - 1) BigInteger.ONE else q.pow(free - 1 - pos)
        val maxS = h - 1 - (free - 1 - pos)
        val next = arrayOfNulls<BigInteger>(h)
        for (prev in 0 until h) {
            val num = layer[prev] ?: continue
            val hi = minOf(maxS, prev + caps[pos][prev])
            var s = prev + 1
            while (s <= hi) {
                val candidate = num.add(coeff.shiftLeft(s))
                val old = next[s]
                if (old == null || candidate > old) next[s] = candidate
                s++
            }
        }
        layer = next
    }
    var best = BigInteger.ZERO
    val lastCap = caps[free]
    for (last in 0 until h) {
        val num = layer[last] ?: continue
        val kLast = h - last
        if (kLast >= 1 && kLast <= lastCap[last] && num > best) best = num
    }
    return best.divide(gap)
}

/**
 * Keeps a halving vector only when `x0` is least, then traces it.
 * The split bound depends only on the step and the sum so far, so this filter is what makes the representative minimal.
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
 * Length 2. The free count `k` is the one with `2^k ≡ -q` modulo the gap, and `k` at most the least-element cap.
 * The gap is odd, so each doubling of the residue is one subtraction.
 */
internal fun forEachLength2(h: Int, q: BigInteger, gap: BigInteger, block: (IntArray) -> Unit) {
    val qMod = q.mod(gap)
    if (qMod.signum() == 0) return
    val cap = maxHalvings(q, BigInteger.ONE, 0)
    val lastK = minOf(cap, h - 1)
    if (lastK < 1) return
    val width = (gap.bitLength() + 31) ushr 5
    val gapLimbs = limbsOf(gap, width)
    val target = limbsOf(gap.subtract(qMod), width)
    val pow = IntArray(width)
    pow[0] = 1
    val ks = IntArray(1)
    for (k in 1..lastK) {
        doubleMod(pow, gapLimbs)
        if (!pow.contentEquals(target)) continue
        ks[0] = k
        if (respectsMinimum(q, ks, h - k)) block(ks)
    }
}

/** `2 * limb` modulo [gap]. The residue starts below the gap, so the product needs at most one subtraction. */
private fun doubleMod(limb: IntArray, gap: IntArray) {
    var carry = 0L
    for (i in limb.indices) {
        val doubled = (u32(limb[i]) shl 1) + carry
        limb[i] = doubled.toInt()
        carry = doubled ushr 32
    }
    if (carry == 0L && lessThan(limb, gap)) return
    var borrow = 0L
    for (i in limb.indices) {
        val diff = u32(limb[i]) - u32(gap[i]) - borrow
        if (diff < 0) {
            limb[i] = (diff + LIMB).toInt()
            borrow = 1
        } else {
            limb[i] = diff.toInt()
            borrow = 0
        }
    }
    check(borrow == carry)
}

/**
 * `ks` is valid only for the duration of [block].
 * Walks `k1,...,k_{L-2}` and solves the next count from `N ≡ 0` modulo [gap].
 * `x0` is the least element, so [maxHalvings] limits every count.
 */
internal fun forEachWalk(
    length: Int,
    h: Int,
    q: BigInteger,
    gap: BigInteger,
    block: (IntArray) -> Unit,
) {
    WalkSearch(length, h, q, gap, block).run()
}

private class WalkSearch(
    private val length: Int,
    private val h: Int,
    private val q: BigInteger,
    private val gap: BigInteger,
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
            val solvedCap = maxHalvings(q, envelope, sum)
            for (k in hits) {
                if (sum + k >= h || k > solvedCap) continue
                prefix[length - 2] = k
                if (!respectsMinimum(q, prefix, h - sum - k)) continue
                block(prefix)
            }
            return
        }
        val slotsLeft = length - 3 - pos
        val room = h - 2 - sum - slotsLeft
        val maxK = minOf(room, maxHalvings(q, envelope, sum))
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
 * Partial sums `0 = s0 < s1 < ... < s_{L-1} < h` split so the walked half is the smaller one when the stored half fits.
 * Left residues are activated in order of `s_left`, and each right continuation
 * starts strictly later, so the two halves do not interleave.
 *
 * [looseCaps] limits every count. The right half is shared by every left state,
 * so those limits depend on the step index and the sum so far.
 * The first right count still depends on the left sum and is filtered per match.
 */
internal fun forEachSplit(
    length: Int,
    h: Int,
    q: BigInteger,
    gap: BigInteger,
    block: (IntArray) -> Unit,
) {
    SplitSearch(length, h, q, gap, block).run()
}

private class SplitSearch(
    private val length: Int,
    private val h: Int,
    private val q: BigInteger,
    private val gap: BigInteger,
    private val block: (IntArray) -> Unit,
) {
    private val free = length - 1
    private val leftCount = storedHalfCount(length, h)
    private val rightCount = free - leftCount
    private val capAt: Array<IntArray>
    private val gapLimbs: IntArray
    private val term: Array<Array<IntArray>>
    private val coeff0: IntArray
    private val residue: ModResidue
    private val byEnd: Array<StateList>
    private val leftSums = IntArray(leftCount)
    private val rightSums = IntArray(rightCount)
    private val partial = IntArray(free)
    private val ks = IntArray(free)
    private val sumBits: Int
    private val sumMask: Long
    private lateinit var active: LongMultimap

    init {
        require(length > WALK_MAX_LENGTH)
        require(h <= MAX_PACKED_H) { "halving count $h does not fit the packed partial sums" }
        sumBits = bitsForSums(h)
        sumMask = (1L shl sumBits) - 1
        require(leftCount * sumBits <= 64) {
            "length $length at h=$h packs $leftCount sums of $sumBits bits"
        }
        capAt = looseCaps(length, h, q)
        val width = (gap.bitLength() + 32) ushr 5
        gapLimbs = limbsOf(gap, width)
        val coeff = Array(length) { BigInteger.ONE }
        var powQ = BigInteger.ONE
        for (j in length - 2 downTo 0) {
            powQ = powQ.multiply(q).mod(gap)
            coeff[j] = powQ
        }
        coeff0 = limbsOf(coeff[0], width)
        val pow2 = Array(h) { BigInteger.ONE }
        for (s in 1 until h) pow2[s] = addMod(pow2[s - 1], pow2[s - 1], gap)
        term = Array(length) { j ->
            if (j == 0) emptyArray() else Array(h) { s ->
                val value = if (s == 0) BigInteger.ZERO else coeff[j].multiply(pow2[s]).mod(gap)
                limbsOf(value, width)
            }
        }
        residue = ModResidue(gapLimbs)
        byEnd = Array(h) { StateList() }
    }

    fun run() {
        residue.set(coeff0)
        genLeft(0, 0)
        var maxStart = 0
        val firstRightCap = capAt[leftCount]
        for (s in 0 until h) {
            val ended = byEnd[s]
            if (ended.size == 0) continue
            val reach = s + firstRightCap[s]
            if (reach > maxStart) maxStart = reach
        }
        var states = 0
        for (bucket in byEnd) states += bucket.size
        active = LongMultimap(states)
        for (start in 1..maxStart) {
            if (start >= h) break
            val ended = byEnd[start - 1]
            for (i in 0 until ended.size) active.add(ended.hash[i], ended.packed[i])
            ended.release()
            if (start + rightCount - 1 > h - 1) continue
            rightSums[0] = start
            residue.set(term[leftCount + 1][start])
            genRight(1, start)
        }
    }

    private fun cappedS(stepsDone: Int, sum: Int, maxS: Int): Int =
        minOf(maxS, sum + capAt[stepsDone][sum])

    private fun genLeft(pos: Int, prev: Int) {
        if (pos == leftCount) {
            byEnd[leftSums[leftCount - 1]].add(residue.hashNegated(), packSums(leftSums, leftCount, sumBits))
            return
        }
        val remainingAfter = (leftCount - 1 - pos) + rightCount
        val maxS = h - 1 - remainingAfter
        for (s in prev + 1..cappedS(pos, prev, maxS)) {
            leftSums[pos] = s
            val add = term[pos + 1][s]
            residue.addMod(add)
            genLeft(pos + 1, s)
            residue.subMod(add)
        }
    }

    private fun genRight(pos: Int, prev: Int) {
        if (pos == rightCount) {
            emit()
            return
        }
        val remainingAfter = rightCount - 1 - pos
        val maxS = h - 1 - remainingAfter
        for (s in prev + 1..cappedS(leftCount + pos, prev, maxS)) {
            rightSums[pos] = s
            val add = term[leftCount + 1 + pos][s]
            residue.addMod(add)
            genRight(pos + 1, s)
            residue.subMod(add)
        }
    }

    private fun emit() {
        val node0 = active.first(residue.hash())
        if (node0 < 0) return
        val last = rightSums[rightCount - 1]
        if (h - last > capAt[free][last]) return
        val start = rightSums[0]
        val firstRight = capAt[leftCount]
        for (i in 0 until rightCount) partial[leftCount + i] = rightSums[i]
        var node = node0
        while (node >= 0) {
            val packed = active.packedAt(node)
            val sLeft = ((packed ushr ((leftCount - 1) * sumBits)) and sumMask).toInt()
            if (start - sLeft <= firstRight[sLeft]) {
                unpackSums(packed, leftCount, partial, 0, sumBits, sumMask)
                var prev = 0
                for (i in partial.indices) {
                    ks[i] = partial[i] - prev
                    prev = partial[i]
                }
                block(ks)
            }
            node = active.next(node)
        }
    }
}

/**
 * One residue modulo a fixed gap.
 * The walk adds a term on the way down and subtracts it on the way back, so this buffer stays put.
 */
internal class ModResidue(private val gap: IntArray) {
    private val limb = IntArray(gap.size)

    fun set(src: IntArray) {
        src.copyInto(limb)
    }

    fun addMod(add: IntArray) {
        var carry = 0L
        for (i in limb.indices) {
            val sum = u32(limb[i]) + u32(add[i]) + carry
            limb[i] = sum.toInt()
            carry = sum ushr 32
        }
        check(carry == 0L)
        if (!lessThan(limb, gap)) subtract(gap)
    }

    /** Inverse of [addMod] for the same addend. */
    fun subMod(add: IntArray) {
        var borrow = 0L
        for (i in limb.indices) {
            val diff = u32(limb[i]) - u32(add[i]) - borrow
            if (diff < 0) {
                limb[i] = (diff + LIMB).toInt()
                borrow = 1
            } else {
                limb[i] = diff.toInt()
                borrow = 0
            }
        }
        if (borrow != 0L) addWrapped(gap)
    }

    fun hash(): Long = hashLimbs(limb)

    /** Hash of the additive inverse. Zero stays zero, matching a stored key of 0 rather than the gap. */
    fun hashNegated(): Long {
        if (isZero()) return hash()
        var mixed = HASH_OFFSET
        var borrow = 0L
        for (i in limb.indices) {
            val diff = u32(gap[i]) - u32(limb[i]) - borrow
            val bits: Int
            if (diff < 0) {
                bits = (diff + LIMB).toInt()
                borrow = 1
            } else {
                bits = diff.toInt()
                borrow = 0
            }
            mixed = mixHash(mixed, bits)
        }
        check(borrow == 0L)
        return avalanche(mixed)
    }

    fun toBigInteger(): BigInteger {
        var n = BigInteger.ZERO
        for (i in limb.lastIndex downTo 0) {
            n = n.shiftLeft(32).or(BigInteger.valueOf(u32(limb[i])))
        }
        return n
    }

    private fun isZero(): Boolean {
        for (bits in limb) if (bits != 0) return false
        return true
    }

    /** Adds [add] and drops a carry out of the last limb. The buffer is below zero by less than the gap. */
    private fun addWrapped(add: IntArray) {
        var carry = 0L
        for (i in limb.indices) {
            val sum = u32(limb[i]) + u32(add[i]) + carry
            limb[i] = sum.toInt()
            carry = sum ushr 32
        }
    }

    private fun subtract(dec: IntArray) {
        var borrow = 0L
        for (i in limb.indices) {
            val diff = u32(limb[i]) - u32(dec[i]) - borrow
            if (diff < 0) {
                limb[i] = (diff + LIMB).toInt()
                borrow = 1
            } else {
                limb[i] = diff.toInt()
                borrow = 0
            }
        }
        check(borrow == 0L)
    }
}

/** Little-endian unsigned limbs. The width has room for one extra bit past the gap. */
internal fun limbsOf(n: BigInteger, width: Int): IntArray {
    require(n.signum() >= 0)
    val out = IntArray(width)
    var rest = n
    val mask = BigInteger.ONE.shiftLeft(32).subtract(BigInteger.ONE)
    for (i in 0 until width) {
        if (rest.signum() == 0) break
        out[i] = rest.and(mask).toInt()
        rest = rest.shiftRight(32)
    }
    require(rest.signum() == 0) { "value does not fit in $width limbs" }
    return out
}

private fun u32(bits: Int): Long = bits.toLong() and 0xffffffffL

private fun lessThan(a: IntArray, b: IntArray): Boolean {
    for (i in a.lastIndex downTo 0) {
        val aa = u32(a[i])
        val bb = u32(b[i])
        if (aa != bb) return aa < bb
    }
    return false
}

private const val LIMB = 0x1_0000_0000L
private const val HASH_OFFSET = -0x61C8864680B583EBL

private fun hashLimbs(src: IntArray): Long {
    var mixed = HASH_OFFSET
    for (bits in src) mixed = mixHash(mixed, bits)
    return avalanche(mixed)
}

private fun mixHash(mixed: Long, bits: Int): Long {
    var x = mixed xor u32(bits)
    x = x xor (x ushr 30)
    x *= -0x40A7B892E31B1A47L
    return x xor (x ushr 27)
}

private fun avalanche(mixed: Long): Long {
    var x = mixed
    x = x xor (x ushr 33)
    x *= -49064778989728563L
    x = x xor (x ushr 33)
    return x
}

/** Left states that end at one partial sum, before they join the active table. */
private class StateList {
    var hash = LongArray(4)
    var packed = LongArray(4)
    var size = 0

    fun add(h: Long, p: Long) {
        if (size == hash.size) {
            val n = size * 2
            hash = hash.copyOf(n)
            packed = packed.copyOf(n)
        }
        hash[size] = h
        packed[size] = p
        size++
    }

    fun release() {
        hash = LongArray(0)
        packed = LongArray(0)
        size = 0
    }
}

/**
 * Packed partial sums grouped by a 64-bit residue hash.
 * Equal hashes share a chain. A colliding pair fails when the numerator is rebuilt.
 */
private class LongMultimap(expected: Int) {
    private var cap: Int
    private var mask: Int
    private var hashes: LongArray
    private var used: BooleanArray
    private var head: IntArray
    private var nextNode: IntArray
    private var packed: LongArray
    private var nodeHash: LongArray
    private var nodeCount = 0
    private var filled = 0

    init {
        var c = 16
        val need = expected.coerceAtLeast(1) * 2
        while (c < need) {
            val grown = c shl 1
            if (grown <= 0) break
            c = grown
        }
        cap = c
        mask = c - 1
        hashes = LongArray(c)
        used = BooleanArray(c)
        head = IntArray(c) { -1 }
        val nodes = expected.coerceAtLeast(1)
        nextNode = IntArray(nodes)
        packed = LongArray(nodes)
        nodeHash = LongArray(nodes)
    }

    fun add(hash: Long, value: Long) {
        if (nodeCount == packed.size) growNodes()
        if (filled * 2 >= cap) growTable()
        val slot = find(hash)
        if (!used[slot]) {
            used[slot] = true
            hashes[slot] = hash
            head[slot] = -1
            filled++
        }
        val n = nodeCount++
        nodeHash[n] = hash
        packed[n] = value
        nextNode[n] = head[slot]
        head[slot] = n
    }

    /** First packed sum with this hash, or -1 when none is stored. */
    fun first(hash: Long): Int {
        val slot = locate(hash)
        if (slot < 0) return -1
        return head[slot]
    }

    fun next(node: Int): Int = nextNode[node]

    fun packedAt(node: Int): Long = packed[node]

    private fun find(hash: Long): Int {
        var i = slot(hash)
        var steps = 0
        while (used[i] && hashes[i] != hash) {
            i = (i + 1) and mask
            check(++steps <= cap)
        }
        return i
    }

    private fun locate(hash: Long): Int {
        var i = slot(hash)
        var steps = 0
        while (used[i]) {
            if (hashes[i] == hash) return i
            i = (i + 1) and mask
            if (++steps > cap) return -1
        }
        return -1
    }

    private fun slot(hash: Long): Int = (hash xor (hash ushr 32)).toInt() and mask

    private fun growNodes() {
        val n = packed.size * 2
        nextNode = nextNode.copyOf(n)
        packed = packed.copyOf(n)
        nodeHash = nodeHash.copyOf(n)
    }

    private fun growTable() {
        val grown = cap shl 1
        if (grown <= 0) return
        val newMask = grown - 1
        val newHashes = LongArray(grown)
        val newUsed = BooleanArray(grown)
        val newHead = IntArray(grown) { -1 }
        var newFilled = 0
        for (n in 0 until nodeCount) {
            val hash = nodeHash[n]
            var i = (hash xor (hash ushr 32)).toInt() and newMask
            var steps = 0
            while (newUsed[i] && newHashes[i] != hash) {
                i = (i + 1) and newMask
                check(++steps <= grown)
            }
            if (!newUsed[i]) {
                newUsed[i] = true
                newHashes[i] = hash
                newFilled++
            }
            nextNode[n] = newHead[i]
            newHead[i] = n
        }
        cap = grown
        mask = newMask
        hashes = newHashes
        used = newUsed
        head = newHead
        filled = newFilled
    }
}

/** Bits needed to store a partial sum strictly below [h]. */
private fun bitsForSums(h: Int): Int {
    val maxSum = h - 1
    if (maxSum <= 1) return 1
    return 32 - maxSum.countLeadingZeroBits()
}

private fun packSums(sums: IntArray, count: Int, sumBits: Int): Long {
    var packed = 0L
    for (i in 0 until count) packed = packed or (sums[i].toLong() shl (i * sumBits))
    return packed
}

private fun unpackSums(
    packed: Long,
    count: Int,
    dest: IntArray,
    offset: Int,
    sumBits: Int,
    sumMask: Long,
) {
    for (i in 0 until count) dest[offset + i] = ((packed ushr (i * sumBits)) and sumMask).toInt()
}

/** Both arguments lie in `0..gap-1`, so the sum needs at most one subtraction. */
private fun addMod(a: BigInteger, b: BigInteger, gap: BigInteger): BigInteger {
    val sum = a.add(b)
    return if (sum.compareTo(gap) >= 0) sum.subtract(gap) else sum
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

/** Greatest integer `r` with `r^degree <= n`, and that power. */
internal fun floorRoot(n: BigInteger, degree: Int): BigInteger = floorRootAndPower(n, degree).first

internal fun floorRootAndPower(n: BigInteger, degree: Int): Pair<BigInteger, BigInteger> {
    require(degree >= 1)
    require(n.signum() >= 0)
    if (degree == 1 || n < BigInteger.TWO) return n to n.pow(degree)
    val bitlen = n.bitLength()
    var workBits = 1
    var root = BigInteger.ONE
    var power = BigInteger.ONE
    while (workBits < bitlen) {
        val nextBits = minOf(bitlen, workBits shl 1)
        val extra = nextBits - workBits
        val top = if (nextBits == bitlen) n else n.shiftRight(bitlen - nextBits)
        // One past the previous root, shifted by ceil(extra/degree), sits strictly above this prefix.
        val grow = (extra + degree - 1) / degree
        val guess = root.add(BigInteger.ONE).shiftLeft(grow)
        val solved = newtonFloorRoot(top, degree, guess)
        root = solved.first
        power = solved.second
        workBits = nextBits
    }
    return root to power
}

private fun newtonFloorRoot(n: BigInteger, degree: Int, start: BigInteger): Pair<BigInteger, BigInteger> {
    var x = if (start.signum() > 0) start else BigInteger.ONE
    val deg = BigInteger.valueOf(degree.toLong())
    val degM1 = BigInteger.valueOf((degree - 1).toLong())
    while (true) {
        val reduced = x.pow(degree - 1)
        val y = if (reduced.signum() == 0) {
            BigInteger.ONE
        } else {
            x.multiply(degM1).add(n.divide(reduced)).divide(deg)
        }
        if (y >= x) break
        x = y
    }
    var power = x.pow(degree)
    while (power > n) {
        x = x.subtract(BigInteger.ONE)
        power = if (x.signum() > 0) x.pow(degree) else BigInteger.ZERO
    }
    if (x.signum() <= 0) return BigInteger.ZERO to BigInteger.ZERO
    val linear = deg.multiply(x.pow(degree - 1))
    if (power.add(linear) > n) return x to power
    while (true) {
        val next = x.add(BigInteger.ONE)
        val nextPower = next.pow(degree)
        if (nextPower > n) return x to power
        x = next
        power = nextPower
    }
}
