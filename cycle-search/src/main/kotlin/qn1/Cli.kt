package qn1

internal enum class SearchMode {
    ANY,
    THROUGH_1,
    FIXED_Q,
    DIGITS,
}

/**
 * One invocation of the search.
 * [length] and [maxH] are set for [SearchMode.ANY] and [SearchMode.THROUGH_1].
 * [q], [minLength], [maxLength], and [checkedOdds] are set for [SearchMode.FIXED_Q].
 * [places] and [digits] are set for [SearchMode.DIGITS].
 * The other fields stay unset when the flag was omitted.
 */
internal data class Cli(
    val mode: SearchMode,
    val length: Int? = null,
    val maxH: Int? = null,
    val q: Int? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val places: Int? = null,
    val digits: String? = null,
    val checkedOdds: Int? = null,
)

private val VALUE_FLAGS = setOf(
    "q", "length", "max-h", "min-length", "max-length", "places", "digits", "checked-odds",
)
private val SWITCHES = setOf("through-1")
private val DIGIT_ROOTS = setOf("cbrt2", "sqrt2")

/** Reads `--name value` and `--name=value`. A bare word is rejected. */
internal fun parseCli(args: Array<String>): Cli {
    val values = mutableMapOf<String, String>()
    val switches = mutableSetOf<String>()
    var i = 0
    while (i < args.size) {
        val token = args[i]
        if (!token.startsWith("--") || token.length == 2) {
            throw IllegalArgumentException("unexpected $token")
        }
        val body = token.removePrefix("--")
        val eq = body.indexOf('=')
        val name = if (eq >= 0) body.substring(0, eq) else body
        val inline = if (eq >= 0) body.substring(eq + 1) else null
        when (name) {
            in SWITCHES -> {
                if (inline != null) throw IllegalArgumentException("--$name takes no value")
                if (!switches.add(name)) throw IllegalArgumentException("repeated --$name")
            }
            in VALUE_FLAGS -> {
                val value = inline ?: args.getOrNull(i + 1)?.takeUnless { it.startsWith("--") }
                    ?: throw IllegalArgumentException("--$name needs a value")
                if (inline == null) i++
                if (name in values) throw IllegalArgumentException("repeated --$name")
                if (value.isEmpty()) throw IllegalArgumentException("--$name needs a value")
                values[name] = value
            }
            else -> throw IllegalArgumentException("unknown --$name")
        }
        i++
    }
    return interpret(values, switches)
}

private fun interpret(values: Map<String, String>, switches: Set<String>): Cli {
    val modes = buildList {
        if ("through-1" in switches) add(SearchMode.THROUGH_1)
        if ("digits" in values) add(SearchMode.DIGITS)
        if ("q" in values) add(SearchMode.FIXED_Q)
    }
    if (modes.size > 1) {
        throw IllegalArgumentException("choose one of --q, --through-1, --digits")
    }
    val mode = modes.singleOrNull() ?: SearchMode.ANY
    val length = optionalInt(values, "length")
    val maxH = optionalInt(values, "max-h")
    val q = optionalInt(values, "q")
    val minLength = optionalInt(values, "min-length")
    val maxLength = optionalInt(values, "max-length")
    val places = optionalInt(values, "places")
    val digits = values["digits"]
    val checkedOdds = optionalInt(values, "checked-odds")
    when (mode) {
        SearchMode.ANY -> {
            reject(values, switches, "q", "min-length", "max-length", "places", "through-1", "digits", "checked-odds")
            if (length == null || maxH == null) {
                throw IllegalArgumentException("--length and --max-h are required")
            }
            requireRange(length, maxH)
            return Cli(mode, length = length, maxH = maxH)
        }
        SearchMode.THROUGH_1 -> {
            reject(values, switches, "q", "min-length", "max-length", "places", "digits", "checked-odds")
            if (length == null || maxH == null) {
                throw IllegalArgumentException("--length and --max-h are required")
            }
            requireRange(length, maxH)
            return Cli(mode, length = length, maxH = maxH)
        }
        SearchMode.FIXED_Q -> {
            reject(values, switches, "length", "max-h", "places", "through-1", "digits")
            if (q == null) throw IllegalArgumentException("--q is required")
            if (maxLength == null) throw IllegalArgumentException("--max-length is required")
            if (checkedOdds == null) throw IllegalArgumentException("--checked-odds is required")
            val first = minLength ?: MIN_CYCLE_LENGTH
            requireOddQ(q)
            if (checkedOdds < 1) throw IllegalArgumentException("--checked-odds must be positive")
            if (first < MIN_CYCLE_LENGTH) throw IllegalArgumentException("--min-length must be at least $MIN_CYCLE_LENGTH")
            if (maxLength < first) throw IllegalArgumentException("--max-length must be at least --min-length")
            return Cli(mode, q = q, minLength = first, maxLength = maxLength, checkedOdds = checkedOdds)
        }
        SearchMode.DIGITS -> {
            reject(values, switches, "q", "length", "max-h", "min-length", "max-length", "through-1", "checked-odds")
            if (digits !in DIGIT_ROOTS) throw IllegalArgumentException("--digits must be cbrt2 or sqrt2")
            if (places == null) throw IllegalArgumentException("--places is required")
            if (places < 1) throw IllegalArgumentException("--places must be positive")
            return Cli(mode, places = places, digits = digits)
        }
    }
}

private fun reject(values: Map<String, String>, switches: Set<String>, vararg names: String) {
    for (name in names) {
        if (name in values || name in switches) throw IllegalArgumentException("--$name does not belong in this command")
    }
}

private fun optionalInt(values: Map<String, String>, name: String): Int? {
    val raw = values[name] ?: return null
    return raw.toIntOrNull() ?: throw IllegalArgumentException("--$name must be an integer")
}

private fun requireRange(length: Int, maxH: Int) {
    if (length < MIN_CYCLE_LENGTH) throw IllegalArgumentException("--length must be at least $MIN_CYCLE_LENGTH")
    if (maxH < 1) throw IllegalArgumentException("--max-h must be positive")
}

private fun requireOddQ(q: Int) {
    if (q < 3 || q % 2 == 0) throw IllegalArgumentException("--q must be an odd integer at least 3")
}
