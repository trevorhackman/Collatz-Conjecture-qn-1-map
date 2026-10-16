package qn1

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/** Heights where the unsplit walk still finishes quickly. */
private const val MAX_H = 40

/** First length that splits the numerator. Lengths below this only walk. */
private const val MIN_LENGTH = 4
private const val MAX_LENGTH = 8

/** Length 7 stops here. The unsplit walk is the slow half of the comparison. */
private const val LENGTH_7_MAX_H = 32

/** Length 8 stops here. The unsplit walk is the slow half of the comparison. */
private const val LENGTH_8_MAX_H = 24

class CycleSearchTest {
    @Test
    fun walkAndSplitAcceptTheSameHalvingVectors() {
        for (length in MIN_LENGTH..MAX_LENGTH) {
            val limit = when (length) {
                7 -> LENGTH_7_MAX_H
                8 -> LENGTH_8_MAX_H
                else -> MAX_H
            }
            for (h in 1..limit) {
                val adm = admissible(h, length) ?: continue
                val walk = walkVectors(length, h, adm.q, adm.gap)
                val split = splitVectors(length, h, adm.q, adm.gap)
                if (walk == split) continue
                fail(
                    "L=$length h=$h walk=${walk.size} split=${split.size} " +
                        "missed=${(walk - split).take(8)} extra=${(split - walk).take(8)}",
                )
            }
        }
    }

    @Test
    fun lengthTwoThroughThirtyFindsTheThreeKnownCycles() {
        assertEquals(
            setOf(
                cycle(5, 5, 1, 3),
                cycle(181, 15, 27, 611),
                cycle(181, 15, 35, 99),
            ),
            searchLength(2, 30),
        )
    }

    @Test
    fun lengthThreeThroughFortyFindsTheTwoKnownCycles() {
        assertEquals(
            setOf(
                cycle(5, 7, 13, 33, 83),
                cycle(5, 7, 17, 43, 27),
            ),
            searchLength(3, 40),
        )
    }

    @Test
    fun lengthFourThroughFortyFindsNone() {
        assertEquals(emptySet(), searchLength(4, 40))
    }

    @Test
    fun throughOneLengthTwoKeepsOnlyTheOrbitOfOne() {
        assertEquals(listOf(cycle(5, 5, 1, 3)), searchThroughOne(2, 30))
    }

    @Test
    fun powerWindowMatchesKnownHeights() {
        assertEquals(5..5, heightsForQ(BigInteger.valueOf(5), 2))
        assertEquals(14..15, heightsForQ(BigInteger.valueOf(5), 6))
        assertTrue(heightsForQ(BigInteger.valueOf(7), 2).isEmpty())
        assertTrue(heightsForQ(BigInteger.valueOf(15), 4).isEmpty())
    }

    @Test
    fun q5ThroughLength4FindsTheThreeKnownCycles() {
        assertEquals(
            setOf(
                cycle(5, 5, 1, 3),
                cycle(5, 7, 13, 33, 83),
                cycle(5, 7, 17, 43, 27),
            ),
            searchFixedQ(5, 4).toSet(),
        )
    }

    @Test
    fun q5Length11WalkMatchesSplit() {
        val q = BigInteger.valueOf(5)
        for (h in heightsForQ(q, 11)) {
            val adm = admissible(h, 11) ?: fail("missing h=$h")
            assertEquals(q, adm.q)
            val walk = walkVectors(11, h, adm.q, adm.gap)
            val split = splitVectors(11, h, adm.q, adm.gap)
            if (walk == split) continue
            fail("h=$h walk=${walk.size} split=${split.size} missed=${(walk - split).take(8)} extra=${(split - walk).take(8)}")
        }
    }
}

private fun walkVectors(length: Int, h: Int, q: BigInteger, gap: BigInteger): Set<String> {
    val keys = linkedSetOf<String>()
    forEachWalk(length, h, q, gap) { ks -> keys.add(ks.joinToString(",")) }
    return keys
}

private fun splitVectors(length: Int, h: Int, q: BigInteger, gap: BigInteger): Set<String> {
    val keys = linkedSetOf<String>()
    forEachSplit(length, h, q, gap) { ks -> keys.add(ks.joinToString(",")) }
    return keys
}

private fun cycle(q: Long, h: Int, vararg elements: Long): Cycle =
    Cycle(BigInteger.valueOf(q), h, elements.map(BigInteger::valueOf))
