package qn1

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.system.measureTimeMillis

/** One row of a search table: every admissible h up to [maxH] at this length. */
internal data class LengthRow(val length: Int, val maxH: Int) {
    /** floor(2^{h/length}) stays below 2 raised to this power. */
    val qBelowExponent: Int get() = maxH / length
}

/** Through 1, as in the computations table. Length 3 is the run through h = 200000. */
internal val THROUGH1_ROWS: List<LengthRow> = listOf(LengthRow(3, 200_000)) +
    (4..20).map { LengthRow(it, 100_000) }

/** Any odd elements, as in the computations table. */
internal val ANY_ROWS: List<LengthRow> = listOf(
    LengthRow(2, 320_000),
    LengthRow(3, 7_000),
    LengthRow(4, 5_500),
    LengthRow(5, 4_500),
    LengthRow(6, 1_400),
    LengthRow(7, 950),
    LengthRow(8, 470),
    LengthRow(9, 320),
)

/** Fixed q. The first entry is q = 5 through length 20; the rest are the odd q from 7 through 25. */
internal val FIXED_Q_ROWS: List<Pair<Int, Int>> = listOf(5 to 20) + (7..25 step 2).map { it to 16 }

/** One Paper II bound table. The four calculations do not share a result. */
internal val BOUND_TABLES = setOf("q3star", "q30", "q2star", "q2")

internal fun runBounds(bound: String, x: Int?) {
    val xs = if (x == null) {
        (1..BOUNDS_MAX_X).filter { it % 2 == 1 }
    } else {
        listOf(x)
    }
    when (bound) {
        "q3star" -> runBoundTable("Q3*") {
            val q3Star = xs.associateWith { log2Q3Star(it) }
            for (x in xs) println(boundLine(x, q3Star.getValue(x)))
        }
        "q30" -> runBoundTable("Q30 j<$Q30_J_MAX") {
            for (x in xs) {
                lateinit var hit: BoundHit
                val ms = measureTimeMillis {
                    hit = q30Hit(x) { j -> System.err.println("Q30 x=$x j=$j/$Q30_J_MAX") }
                }
                println(boundLine(x, hit.log2) + " j=${hit.j} r=${hit.r} in ${seconds(ms)}s")
            }
        }
        "q2star" -> runBoundTable("Q2*") {
            val q2Star = xs.associateWith { log2Q2Star(it) }
            for (x in xs) println(boundLine(x, q2Star.getValue(x)))
        }
        "q2" -> if (x == null) {
            runBoundTable("Q2 odd x through $Q2_TABLE_MAX_X, j<$Q2_CONV_SCAN_LIMIT") {
                for (row in q2Table()) {
                    println(boundLine(row.x, row.hit.log2) + " j0=${row.j0} j=${row.hit.j}")
                }
            }
        } else {
            runBoundTable("Q2 through j<$SQRT2_SEARCH_LIMIT") {
                for (x in xs) {
                    var j0 = 0
                    var hit = BoundHit(BigDecimal.ZERO, -1, 1)
                    val ms = measureTimeMillis {
                        j0 = j0Sqrt2(x)
                        hit = q20Hit(x, j0)
                    }
                    println(boundLine(x, hit.log2) + " j0=$j0 j=${hit.j} in ${seconds(ms)}s")
                }
            }
        }
        else -> usage()
    }
}

private fun runBoundTable(label: String, block: () -> Unit) {
    println(label)
    val ms = measureTimeMillis(block)
    println("$label in ${seconds(ms)}s")
}

private fun seconds(ms: Long): String = (ms / 1000.0).toString()

private fun boundLine(x: Int, log: BigDecimal): String {
    val shown = log.setScale(12, RoundingMode.HALF_UP).toPlainString()
    return "x=$x log2=$shown 2^${powerAbove(log)}"
}

internal fun runTables(table: String, length: Int?, maxH: Int?, q: Int?, maxLength: Int?) {
    when (table) {
        "through1" -> for (row in selectedRows(THROUGH1_ROWS, length, maxH)) {
            println(formatSweep("through 1", row, searchThroughOne(row.length, row.maxH)))
        }
        "any" -> for (row in selectedRows(ANY_ROWS, length, maxH)) {
            println(formatSweep("any", row, searchLength(row.length, row.maxH)))
        }
        "q" -> runFixedQTable(q, maxLength)
        else -> usage()
    }
}

private fun selectedRows(rows: List<LengthRow>, length: Int?, maxH: Int?): List<LengthRow> {
    if (length == null && maxH == null) return rows
    if (length == null) usage()
    val published = rows.firstOrNull { it.length == length }
    val limit = maxH ?: published?.maxH ?: usage()
    if (length < MIN_CYCLE_LENGTH || limit < 1) usage()
    return listOf(LengthRow(length, limit))
}

private fun runFixedQTable(q: Int?, maxLength: Int?) {
    val rows = if (q == null) {
        if (maxLength != null) usage()
        FIXED_Q_ROWS
    } else {
        val published = FIXED_Q_ROWS.firstOrNull { it.first == q }?.second
        val limit = maxLength ?: published ?: usage()
        listOf(q to limit)
    }
    for ((multiplier, longest) in rows) {
        val cycles = searchFixedQ(multiplier, longest)
        println("q=$multiplier lengths 2 through $longest: ${cycles.size} cycles")
    }
}

internal fun formatSweep(kind: String, row: LengthRow, cycles: Collection<Cycle>): String {
    val ordered = cycles.sortedWith(compareBy({ it.q }, { it.h }, { it.elements.minOrNull() }))
    val head = "$kind L=${row.length} h<=${row.maxH}: ${ordered.size} cycles, q below 2^${row.qBelowExponent}"
    if (ordered.isEmpty()) return head
    return head + "\n" + ordered.joinToString("\n") { "  $it" }
}
