package qn1

internal enum class SearchMode {
    ANY,
    THROUGH_1,
    FIXED_Q,
    BOUNDS,
    TABLES,
    DIGITS,
}

/**
 * One invocation of the search.
 * [length] and [maxH] are set for [SearchMode.ANY] and [SearchMode.THROUGH_1].
 * [q] and [maxLength] are set for [SearchMode.FIXED_Q].
 * [places] is set for [SearchMode.DIGITS].
 * [table] is set for [SearchMode.TABLES]. The other fields stay unset when the flag was omitted.
 */
internal data class Cli(
    val mode: SearchMode,
    val length: Int? = null,
    val maxH: Int? = null,
    val q: Int? = null,
    val maxLength: Int? = null,
    val x: Int? = null,
    val places: Int? = null,
    val table: String? = null,
)

private val VALUE_FLAGS = setOf("q", "length", "max-h", "max-length", "x", "places", "tables")
private val SWITCHES = setOf("through-1", "bounds", "digits")

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
        if ("bounds" in switches) add(SearchMode.BOUNDS)
        if ("digits" in switches) add(SearchMode.DIGITS)
        if ("tables" in values) add(SearchMode.TABLES)
        if ("q" in values && "tables" !in values) add(SearchMode.FIXED_Q)
    }
    if (modes.size > 1) throw IllegalArgumentException("choose one of --q, --through-1, --bounds, --tables, --digits")
    val mode = modes.singleOrNull() ?: SearchMode.ANY
    val length = optionalInt(values, "length")
    val maxH = optionalInt(values, "max-h")
    val q = optionalInt(values, "q")
    val maxLength = optionalInt(values, "max-length")
    val x = optionalInt(values, "x")
    val places = optionalInt(values, "places")
    val table = values["tables"]
    when (mode) {
        SearchMode.ANY -> {
            reject(values, switches, "q", "max-length", "x", "places", "tables", "through-1", "bounds", "digits")
            requireRange(length ?: DEFAULT_LENGTH, maxH ?: DEFAULT_MAX_H)
            return Cli(mode, length = length ?: DEFAULT_LENGTH, maxH = maxH ?: DEFAULT_MAX_H)
        }
        SearchMode.THROUGH_1 -> {
            reject(values, switches, "q", "max-length", "x", "places", "tables", "bounds", "digits")
            requireRange(length ?: THROUGH1_DEFAULT_LENGTH, maxH ?: THROUGH1_DEFAULT_MAX_H)
            return Cli(
                mode,
                length = length ?: THROUGH1_DEFAULT_LENGTH,
                maxH = maxH ?: THROUGH1_DEFAULT_MAX_H,
            )
        }
        SearchMode.FIXED_Q -> {
            reject(values, switches, "length", "max-h", "x", "places", "tables", "through-1", "bounds", "digits")
            val multiplier = q ?: Q_DEFAULT
            val longest = maxLength ?: Q_DEFAULT_MAX_LENGTH
            requireOddQ(multiplier)
            if (longest < MIN_CYCLE_LENGTH) throw IllegalArgumentException("--max-length must be at least $MIN_CYCLE_LENGTH")
            return Cli(mode, q = multiplier, maxLength = longest)
        }
        SearchMode.BOUNDS -> {
            reject(values, switches, "q", "length", "max-h", "max-length", "places", "tables", "through-1", "digits")
            if (x != null && (x < 1 || x % 2 == 0)) throw IllegalArgumentException("--x must be a positive odd integer")
            return Cli(mode, x = x)
        }
        SearchMode.DIGITS -> {
            reject(values, switches, "q", "length", "max-h", "max-length", "x", "tables", "through-1", "bounds")
            val count = places ?: DIGIT_PLACES
            if (count < 1) throw IllegalArgumentException("--places must be positive")
            return Cli(mode, places = count)
        }
        SearchMode.TABLES -> {
            reject(values, switches, "x", "places", "through-1", "bounds", "digits")
            when (table) {
                "through1", "any" -> {
                    reject(values, switches, "q", "max-length")
                    if (maxH != null && length == null) throw IllegalArgumentException("--max-h needs --length")
                    if (length != null && length < MIN_CYCLE_LENGTH) {
                        throw IllegalArgumentException("--length must be at least $MIN_CYCLE_LENGTH")
                    }
                    if (maxH != null && maxH < 1) throw IllegalArgumentException("--max-h must be positive")
                    return Cli(mode, table = table, length = length, maxH = maxH)
                }
                "q" -> {
                    reject(values, switches, "length", "max-h")
                    if (maxLength != null && q == null) throw IllegalArgumentException("--max-length needs --q")
                    if (q != null) requireOddQ(q)
                    if (maxLength != null && maxLength < MIN_CYCLE_LENGTH) {
                        throw IllegalArgumentException("--max-length must be at least $MIN_CYCLE_LENGTH")
                    }
                    return Cli(mode, table = table, q = q, maxLength = maxLength)
                }
                else -> throw IllegalArgumentException("--tables must be through1, any, or q")
            }
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
