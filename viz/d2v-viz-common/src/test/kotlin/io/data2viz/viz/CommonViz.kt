package io.data2viz.viz

data class Domain(val val1: Double, val val2: Double)

fun VizContext.commonViz() {

    val data = listOf(
            Domain(10.0, 10.0),
            Domain(20.0, 40.0),
            Domain(30.0, 90.0)
    )

    data.forEach { datum ->
        circle {
            cx = datum.val1
            cy = datum.val2
            radius = 5.0
        }

    }

}