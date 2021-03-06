package io.data2viz.hierarchy.treemap

import io.data2viz.hierarchy.TreemapNode

/**
 * Like treemapSquarify, except preserves the topology (node adjacencies) of the previous layout computed by
 * treemapResquarify, if there is one and it used the same target aspect ratio.
 * This tiling method is good for animating changes to treemaps because it only changes node sizes and
 * not their relative positions, thus avoiding distracting shuffling and occlusion.
 * The downside of a stable update, however, is a suboptimal layout for subsequent updates: only the first layout
 * uses the Bruls et al. squarified algorithm.
 */
// TODO
fun treemapResquarify(parent: TreemapNode<*>, x0:Double, y0:Double, x1:Double, y1:Double) {
    /*if ((rows = parent._squarify) && (rows.ratio === ratio)) {
        var rows,
        row,
        nodes,
        i,
        j = -1,
        n,
        m = rows.length,
        value = parent.value;

        while (++j < m) {
            row = rows[j], nodes = row.children;
            for (i = row.value = 0, n = nodes.length; i < n; ++i) row.value += nodes[i].value;
            if (row.dice) treemapDice(row, x0, y0, x1, y0 += (y1 - y0) * row.value / value);
            else treemapSlice(row, x0, y0, x0 += (x1 - x0) * row.value / value, y1);
            value -= row.value;
        }
    } else {
        parent._squarify = rows = squarifyRatio(ratio, parent, x0, y0, x1, y1);
        rows.ratio = ratio;
    }*/
}