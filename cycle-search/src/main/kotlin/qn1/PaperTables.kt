package qn1

import java.math.BigDecimal
import java.math.RoundingMode

/** One row of a search table: every admissible h up to [maxH] at this length. */
internal data class LengthRow(val length: Int, val maxH: Int) {
    /** floor(2^{h/length}) stays below 2 raised to this power. */
    val qBelowExponent: Int get() = maxH / length
}

/** Through 1, as in the computations table. Length 3 is the run through h = 200000. */
internal val THROUGH1_ROWS: List<LengthRow> = listOf(LengthRow(3, 200_000)) +
    (4..15).map { LengthRow(it, 100_000) }

/** Any odd elements, as in the computations table. */
internal val ANY_ROWS: List<LengthRow> = listOf(
    LengthRow(2, 8_000),
    LengthRow(3, 4_000),
    LengthRow(4, 3_000),
    LengthRow(5, 2_000),
    LengthRow(6, 1_800),
    LengthRow(7, 700),
    LengthRow(8, 560),
    LengthRow(9, 270),
)

/** Fixed q. The first entry is q = 5 through length 20; the rest are the odd q from 7 through 25. */
internal val FIXED_Q_ROWS: List<Pair<Int, Int>> = listOf(5 to 20) + (7..25 step 2).map { it to 16 }

internal fun runBounds(args: Array<String>) {
    val numbers = args.drop(1)
    if (numbers.size > 1) usage()
    val xs = if (numbers.isEmpty()) {
        (1..BOUNDS_MAX_X).filter { it % 2 == 1 }
    } else {
        val x = numbers[0].toIntOrNull() ?: usage()
        if (x < 1 || x % 2 == 0) usage()
        listOf(x)
    }
    println("Q3*")
    val q3Star = xs.associateWith { log2Q3Star(it) }
    for (x in xs) println(boundLine(x, q3Star.getValue(x)))
    println("Q30 j<$Q30_J_MAX")
    for (x in xs) {
        val hit = q30Hit(x) { j -> System.err.println("Q30 x=$x j=$j/$Q30_J_MAX") }
        println(boundLine(x, hit.log2) + " j=${hit.j} r=${hit.r}")
    }
    println("Q2*")
    val q2Star = xs.associateWith { log2Q2Star(it) }
    for (x in xs) println(boundLine(x, q2Star.getValue(x)))
    println("Q2 through j<$SQRT2_SEARCH_LIMIT")
    for (x in xs) {
        val j0 = j0Sqrt2(x)
        val hit = q20Hit(x, j0)
        println(boundLine(x, hit.log2) + " j0=$j0 j=${hit.j}")
    }
}

private fun boundLine(x: Int, log: BigDecimal): String {
    val shown = log.setScale(12, RoundingMode.HALF_UP).toPlainString()
    return "x=$x log2=$shown 2^${powerAbove(log)}"
}

internal fun runTables(args: Array<String>) {
    if (args.size < 3 || args[1] != "i") usage()
    val rest = args.drop(3)
    when (args[2]) {
        "through1" -> for (row in selectedRows(THROUGH1_ROWS, rest)) {
            println(formatSweep("through 1", row, searchThroughOne(row.length, row.maxH)))
        }
        "any" -> for (row in selectedRows(ANY_ROWS, rest)) {
            println(formatSweep("any", row, searchLength(row.length, row.maxH)))
        }
        "q" -> runFixedQTable(rest)
        else -> usage()
    }
}

private fun selectedRows(rows: List<LengthRow>, args: List<String>): List<LengthRow> {
    if (args.size > 2) usage()
    if (args.isEmpty()) return rows
    val length = args[0].toIntOrNull() ?: usage()
    val published = rows.firstOrNull { it.length == length }
    val maxH = if (args.size == 2) args[1].toIntOrNull() ?: usage() else published?.maxH ?: usage()
    if (length < MIN_CYCLE_LENGTH || maxH < 1) usage()
    return listOf(LengthRow(length, maxH))
}

private fun runFixedQTable(args: List<String>) {
    if (args.size > 2) usage()
    val rows = if (args.isEmpty()) {
        FIXED_Q_ROWS
    } else {
        val q = args[0].toIntOrNull() ?: usage()
        val published = FIXED_Q_ROWS.firstOrNull { it.first == q }?.second
        val maxLength = if (args.size == 2) args[1].toIntOrNull() ?: usage() else published ?: usage()
        listOf(q to maxLength)
    }
    for ((q, maxLength) in rows) {
        val cycles = searchFixedQ(q, maxLength)
        println("q=$q lengths 2 through $maxLength: ${cycles.size} cycles")
    }
}

internal fun formatSweep(kind: String, row: LengthRow, cycles: Collection<Cycle>): String {
    val ordered = cycles.sortedWith(compareBy({ it.q }, { it.h }, { it.elements.minOrNull() }))
    val head = "$kind L=${row.length} h<=${row.maxH}: ${ordered.size} cycles, q below 2^${row.qBelowExponent}"
    if (ordered.isEmpty()) return head
    return head + "\n" + ordered.joinToString("\n") { "  $it" }
}
