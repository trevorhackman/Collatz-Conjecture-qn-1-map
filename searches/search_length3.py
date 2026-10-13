#!/usr/bin/env python3
"""
Search for 3-cycles of the odd-to-odd map C(n) = (q*n + 1) / 2^v,
for odd q >= 3, including cycles that do not pass through 1.

For any 3-cycle, Lemma 10 of the paper gives q = floor(2^{h/3}) and
    delta - 1/3 = sigma/3 + O(1/q),   sigma = 1/x0 + 1/x1 + 1/x2.
So each h not divisible by 3 gives one candidate q. We test every odd
start up to a bound X. An h is skipped only when delta > 1/3, q > 200,
and the window forces every element above X (so a cycle with an element
<= X is impossible).

Known 3-cycles (all found by this search):
    q = 5: 13 -> 33 -> 83,  17 -> 43 -> 27

This bounds the smallest element. It does not prove no other 3-cycle exists.

Usage: python3 search_length3.py [H] [X]
Defaults: H = 10000, X = 40.
"""
import sys
import time


def icbrt(n):
    x = 1 << (n.bit_length() // 3 + 1)
    while True:
        y = (2 * x + n // (x * x)) // 3
        if y >= x:
            break
        x = y
    while (x + 1) ** 3 <= n:
        x += 1
    while x * x * x > n:
        x -= 1
    return x


def v2(n):
    return (n & -n).bit_length() - 1


def search(H, X):
    found = set()
    for h in range(1, H + 1):
        if h % 3 == 0:
            continue
        N = 1 << h
        q = icbrt(N)
        if q % 2 == 0:
            q -= 1
        if q < 3:
            continue
        # window: sigma ≈ num / (27 q^2); skip if every element must exceed X
        num = 27 * N - (3 * q + 1) ** 3
        if num > 0 and q > 200 and num * X < 27 * q * q:
            continue
        x = 3
        while x <= X:
            a = q * x + 1
            if a & 1 == 0:
                y = a >> v2(a)
                if y >= 3 and y != x:
                    b = q * y + 1
                    if b & 1 == 0:
                        z = b >> v2(b)
                        if z >= 3 and z != x and z != y:
                            c = q * z + 1
                            if c & 1 == 0 and (c >> v2(c)) == x:
                                found.add((q, tuple(sorted((x, y, z)))))
            x += 2
    return found


def main():
    H = int(sys.argv[1]) if len(sys.argv) > 1 else 10000
    X = int(sys.argv[2]) if len(sys.argv) > 2 else 40
    t0 = time.time()
    found = search(H, X)
    print(f"h <= {H} (q < 2^{H // 3}), element <= {X}: "
          f"{len(found)} cycles in {time.time() - t0:.1f}s")
    for q, xyz in sorted(found):
        print(q, xyz)


if __name__ == "__main__":
    main()
