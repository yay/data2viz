package io.data2viz.hierarchy.pack

import kotlin.math.max
import kotlin.math.sqrt
import io.data2viz.hierarchy.PackNode
import io.data2viz.hierarchy.packNode

val epsilon = 1e-6

fun <D> packEnclose(circles:List<PackNode<D>>):Double {
    if (circles.isEmpty()) return .0

    val n = circles.size

    // Place the first circle.
    var a = circles.first()
    a.x = .0
    a.y = .0
    if (n < 2) return a.r

    // Place the second circle.
    var b = circles[1]
    a.x = -b.r
    b.x = a.r
    b.y = .0
    if (n < 3) return a.r + b.r

    // Place the third circle.
    var c = circles[2]
    place(b, a, c)

    // Initialize the front-chain using the first three circles a, b and c.
    a = packNode(a)
    b = packNode(b)
    c = packNode(c)
    a.next = b
    c.previous = b
    b.next = c
    a.previous = c
    c.next = a
    b.previous = a

    // Attempt to place each remaining circle…
    var i = 3
    pack@ while (i < n) {
        c = circles[i]
        i++
        place(a, b, c)

        // Find the closest intersecting circle on the front-chain, if any.
        // “Closeness” is determined by linear distance along the front-chain.
        // “Ahead” or “behind” is likewise determined by linear distance.
        var j = b.next!!
        var k = a.previous!!
        var sj = b.r
        var sk = a.r
        do {
            if (sj <= sk) {
                if (intersects(j, c)) {
                    b = j
                    a.next = b
                    b.previous = a
                    i--
                    continue@pack
                }
                sj += j.r
                j = j.next!!
            } else {
                if (intersects(k, c)) {
                    a = k
                    a.next = b
                    b.previous = a
                    i--
                    continue@pack
                }
                sk += k.r
                k = k.previous!!
            }
        } while (j !== k.next)

        // Success! Insert the new circle c between a and b.
        c.previous = a
        c.next = b
        b = c
        b.previous = c
        a.next = c

        // Compute the new closest circle pair to the centroid.
        var aa = score(a)
        c = c.next!!
        while (c !== b) {
            val ca = score(c)
            if (ca < aa) {
                a = c
                aa = ca
            }
            c = c.next!!
        }
        b = a.next!!
    }

    // Compute the enclosing circle of the front chain.
    val chain = mutableListOf(b)
    c = b
    c = c.next!!
    while (c != b) {
        chain.add(c)
        c = c.next!!
    }
    val circle = enclose(chain)!!

    // Translate the circles to put the enclosing circle around the origin.
    (0 until n).forEach {
        a = circles[it]
        a.x -= circle.x
        a.y -= circle.y
    }

    return circle.r
}

private fun <D> score(node: PackNode<D>): Double {
    val a = node
    val b = node.next!!
    val ab = a.r + b.r
    val dx = (a.x * b.r + b.x * a.r) / ab
    val dy = (a.y * b.r + b.y * a.r) / ab
    return dx * dx + dy * dy
}

/*private fun <D> distance2(a: PackNode<D>, x: Double, y: Double): Double {
    val b = a.next!!
    val ab = a.r + b.r
    val dx = (a.x * b.r + b.x * a.r) / ab - x
    val dy = (a.y * b.r + b.y * a.r) / ab - y
    return dx * dx + dy * dy
}*/

private fun <D> intersects(a: PackNode<D>, b: PackNode<D>): Boolean {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val dr = a.r + b.r
    return dr * dr - epsilon > dx * dx + dy * dy;
}

private fun <D> place(a:PackNode<D>, b:PackNode<D>, c:PackNode<D>) {
    val ax = a.x
    val ay = a.y
    var da = b.r + c.r
    var db = a.r + c.r
    val dx = b.x - ax
    val dy = b.y - ay
    val dc = (dx * dx) + (dy * dy)
    if (dc != .0) {
        db *= db
        da *= da
        val x = 0.5 + (db - da) / (2 * dc)
        val temp1: Double = 2.0 * da * (db + dc)
        db -= dc
        val y = sqrt(max(.0, temp1 - db * db - da * da)) / (2 * dc)
        c.x = ax + x * dx + y * dy
        c.y = ay + x * dy - y * dx
    } else {
        c.x = ax + db
        c.y = ay
    }
}

/*function place(a, b, c) {
    var ax = a.x,
    ay = a.y,
    da = b.r + c.r,
    db = a.r + c.r,
    dx = b.x - ax,
    dy = b.y - ay,
    dc = dx * dx + dy * dy;
    if (dc) {
        var x = 0.5 + ((db *= db) - (da *= da)) / (2 * dc),
        2 * da * (db + dc)
        db -= dc
        db * db - da * da
        y = Math.sqrt(Math.max(0, (2 * da * (db + dc)) - ((db -= dc) * db) - (da * da))) / (2 * dc);
        c.x = ax + x * dx + y * dy;
        c.y = ay + x * dy - y * dx;
    } else {
        c.x = ax + db;
        c.y = ay;
    }
}*/