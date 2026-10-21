package qn1

import java.math.BigInteger
import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/** Values of h where the unsplit walk still finishes quickly. */
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
    fun length2ScanAgreesWithTheWalk() {
        for (h in 1..160) {
            val adm = admissible(h, 2) ?: continue
            val walk = walkVectors(2, h, adm.q, adm.gap)
            val scan = linkedSetOf<String>()
            forEachLength2(h, adm.q, adm.gap) { ks -> scan.add(ks.joinToString(",")) }
            if (walk == scan) continue
            fail("h=$h walk=$walk scan=$scan")
        }
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
        assertEquals(listOf(cycle(5, 5, 1, 3)), searchThroughOne(2, 200))
    }

    @Test
    fun floorRootIsTheGreatestPowerThatFits() {
        val values = listOf(
            BigInteger.ONE,
            BigInteger.TWO,
            BigInteger.TEN,
            BigInteger.valueOf(999_999),
            BigInteger("12345678901234567890123456789012345678901234567890"),
            BigInteger.ONE.shiftLeft(80).subtract(BigInteger.ONE),
            BigInteger.ONE.shiftLeft(240).add(BigInteger.valueOf(17)),
            BigInteger.ONE.shiftLeft(2_000),
        )
        for (degree in 1..12) {
            for (n in values) {
                val (root, power) = floorRootAndPower(n, degree)
                assertEquals(power, root.pow(degree), "power degree=$degree bits=${n.bitLength()}")
                assertTrue(power <= n, "low degree=$degree bits=${n.bitLength()}")
                assertTrue(
                    root.add(BigInteger.ONE).pow(degree) > n,
                    "high degree=$degree bits=${n.bitLength()}",
                )
            }
        }
    }

    @Test
    fun powerWindowMatchesKnownH() {
        assertEquals(5..5, hForQ(BigInteger.valueOf(5), 2))
        assertEquals(14..15, hForQ(BigInteger.valueOf(5), 6))
        assertTrue(hForQ(BigInteger.valueOf(7), 2).isEmpty())
        assertTrue(hForQ(BigInteger.valueOf(15), 4).isEmpty())
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
    fun q5FromLength3SkipsTheLength2Cycle() {
        assertEquals(
            setOf(
                cycle(5, 7, 13, 33, 83),
                cycle(5, 7, 17, 43, 27),
            ),
            searchFixedQ(5, 4, minLength = 3).toSet(),
        )
    }

    @Test
    fun leastElementTraceFindsTheKnownCycles() {
        assertEquals(setOf(cycle(5, 5, 1, 3)), traceAt(5, 2).toSet())
        assertEquals(
            setOf(
                cycle(5, 7, 13, 33, 83),
                cycle(5, 7, 17, 43, 27),
            ),
            traceAt(5, 3).toSet(),
        )
        assertEquals(
            setOf(
                cycle(181, 15, 27, 611),
                cycle(181, 15, 35, 99),
            ),
            traceAt(181, 2).toSet(),
        )
    }

    @Test
    fun q181Length16BoundRulesTheCycleOut() {
        val q = BigInteger.valueOf(181)
        assertEquals(120..120, hForQ(q, 16))
        val adm = admissible(120, 16) ?: fail("missing h=120")
        assertEquals(q, adm.q)
        assertEquals(BigInteger.valueOf(44), maxLeastElement(16, 120, adm.q, adm.gap))
        assertEquals(emptyList(), searchFixedQ(181, 16, minLength = 16))
    }

    @Test
    fun q1093Length21HasNoCycle() {
        val q = BigInteger.valueOf(1093)
        assertEquals(212..212, hForQ(q, 21))
        val adm = admissible(212, 21) ?: fail("missing h=212")
        assertEquals(q, adm.q)
        assertEquals(BigInteger.ZERO, maxLeastElement(21, 212, adm.q, adm.gap))
        assertEquals(emptyList(), searchFixedQ(1093, 21))
    }

    @Test
    fun modResidueMatchesBigInteger() {
        val random = Random(1)
        val gaps = listOf(
            BigInteger.valueOf(3),
            BigInteger.ONE.shiftLeft(17).subtract(BigInteger.ONE),
            BigInteger.ONE.shiftLeft(32),
            BigInteger.ONE.shiftLeft(32).subtract(BigInteger.ONE),
            BigInteger("12345678901234567890123456789012345678901"),
        )
        for (gap in gaps) {
            val width = (gap.bitLength() + 32) ushr 5
            val gapLimbs = limbsOf(gap, width)
            val residue = ModResidue(gapLimbs)
            repeat(40) {
                val a = BigInteger(gap.bitLength() + 8, random).mod(gap)
                val b = BigInteger(gap.bitLength() + 8, random).mod(gap)
                residue.set(limbsOf(a, width))
                assertEquals(a, residue.toBigInteger())
                residue.addMod(limbsOf(b, width))
                assertEquals(a.add(b).mod(gap), residue.toBigInteger())
                residue.subMod(limbsOf(b, width))
                assertEquals(a, residue.toBigInteger())
                val neg = if (a.signum() == 0) BigInteger.ZERO else gap.subtract(a)
                val negated = ModResidue(gapLimbs)
                negated.set(limbsOf(neg, width))
                assertEquals(negated.hash(), residue.hashNegated())
            }
        }
    }

    @Test
    fun q5Through20FindsTheThreeKnownCycles() {
        assertEquals(
            setOf(
                cycle(5, 5, 1, 3),
                cycle(5, 7, 13, 33, 83),
                cycle(5, 7, 17, 43, 27),
            ),
            searchFixedQ(5, 20).toSet(),
        )
    }

    @Test
    fun q5Length11WalkMatchesSplit() {
        val q = BigInteger.valueOf(5)
        for (h in hForQ(q, 11)) {
            val adm = admissible(h, 11) ?: fail("missing h=$h")
            assertEquals(q, adm.q)
            val walk = walkVectors(11, h, adm.q, adm.gap)
            val split = splitVectors(11, h, adm.q, adm.gap)
            if (walk == split) continue
            fail("h=$h walk=${walk.size} split=${split.size} missed=${(walk - split).take(8)} extra=${(split - walk).take(8)}")
        }
    }
}

private fun traceAt(q: Int, length: Int): List<Cycle> {
    val qBig = BigInteger.valueOf(q.toLong())
    val found = mutableListOf<Cycle>()
    for (h in hForQ(qBig, length)) {
        val adm = admissible(h, length) ?: fail("missing h=$h")
        assertEquals(qBig, adm.q)
        found += traceByLeastElement(length, h, adm.q, adm.gap)
    }
    return found
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
