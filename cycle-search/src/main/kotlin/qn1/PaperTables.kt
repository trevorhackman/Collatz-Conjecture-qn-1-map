package qn1

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

internal fun formatSweep(kind: String, row: LengthRow, cycles: Collection<Cycle>): String {
    val ordered = cycles.sortedWith(compareBy({ it.q }, { it.h }, { it.elements.minOrNull() }))
    val head = "$kind L=${row.length} h<=${row.maxH}: ${ordered.size} cycles, q below 2^${row.qBelowExponent}"
    if (ordered.isEmpty()) return head
    return head + "\n" + ordered.joinToString("\n") { "  $it" }
}
