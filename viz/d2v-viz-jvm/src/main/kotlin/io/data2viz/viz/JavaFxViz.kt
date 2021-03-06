package io.data2viz.viz

import io.data2viz.color.Color
import io.data2viz.color.colors
import io.data2viz.color.d2vColor
import io.data2viz.color.jfxColor
import io.data2viz.core.CssClass
import io.data2viz.path.PathAdapter
import io.data2viz.path.SvgPath
import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.scene.text.Text
import kotlin.reflect.KProperty

typealias jxShape = javafx.scene.shape.Shape

/**
 * Bootstrap a VizContext in JavaFx environment
 */
fun Group.viz(init: VizContext.() -> Unit): VizContext {


    val vizContext = ParentElement(this)
    init(vizContext)
    return vizContext
}

class ParentElement(val parent: Group) : VizContext, 
        StyledElement by StyleDelegate(parent),
        Transformable by TransformNodeDelegate(parent){
    
    override fun path(init: PathVizItem.() -> Unit): PathVizItem {
        val path = SVGPath()
        val svgPath = SvgPath()
        val item = PathVizJfx(path, svgPath)
        init(item)
        path.content = svgPath.path
        parent.children.add(path)
        return item
    }

    override fun setStyle(style: String) {
        parent.style = style
    }


    override fun circle(init: CircleVizItem.() -> Unit): CircleVizItem {
        val circle = Circle()
        parent.children.add(circle)
        val item = CircleVizJfx(parent, circle)
        init(item)
        return item
    }

    override fun group(init: ParentItem.() -> Unit): ParentItem {
        val group = ParentElement(Group())
        init(group)
        parent.children.add(group.parent)
        return  group
    }

    override fun line(init: LineVizItem.() -> Unit): LineVizItem {
        val line = Line()
        parent.children.add(line)
        val item = LineVizJfx(parent, line)
        init(item)
        return item
    }

    override fun rect(init: RectVizItem.() -> Unit): RectVizItem {
        val rectangle = Rectangle()
        parent.children.add(rectangle)
        val item = RectVizJfx(parent, rectangle)
        init(item)
        return item
    }

    override fun text(init: TextVizItem.() -> Unit): TextVizItem {
        val text = Text()
        parent.children.add(text)
        val item = TextVizJfx(parent, text)
        init(item)
        return  item
    }
}


class PathVizJfx(path: SVGPath, svgPath: SvgPath) : PathVizItem,
        HasFill by FillDelegate(path),
        HasStroke by StrokeDelegate(path),
        Transformable by TransformNodeDelegate(path),
        PathAdapter by svgPath
    

class CircleVizJfx(val parent: Group, val circle: Circle) : CircleVizItem,
        HasFill by FillDelegate(circle),
        StyledElement by StyleDelegate(circle),
        HasStroke by StrokeDelegate(circle),
        Transformable by TransformNodeDelegate(circle)
{

    override var cx: Double by DoublePropertyDelegate(circle.centerXProperty())
    override var cy: Double by DoublePropertyDelegate(circle.centerYProperty())
    override var radius: Double by DoublePropertyDelegate(circle.radiusProperty())
}

class LineVizJfx(val parent: Group, val line: Line) : LineVizItem,
        StyledElement by StyleDelegate(line),
        HasFill by FillDelegate(line),
        HasStroke by StrokeDelegate(line),
        Transformable by TransformNodeDelegate(line) {
    override var x1: Double by DoublePropertyDelegate(line.startXProperty())
    override var y1: Double by DoublePropertyDelegate(line.startYProperty())
    override var x2: Double by DoublePropertyDelegate(line.endXProperty())
    override var y2: Double by DoublePropertyDelegate(line.endYProperty())
}

class RectVizJfx(val parent: Group, val rectangle: Rectangle) : RectVizItem,
        StyledElement by StyleDelegate(rectangle),
        HasFill by FillDelegate(rectangle),
        HasStroke by StrokeDelegate(rectangle),
        Transformable by TransformNodeDelegate(rectangle)
{
    override var x: Double by DoublePropertyDelegate(rectangle.xProperty())
    override var y: Double by DoublePropertyDelegate(rectangle.yProperty())
    override var width: Double by DoublePropertyDelegate(rectangle.widthProperty())
    override var height: Double by DoublePropertyDelegate(rectangle.heightProperty())
    override var rx: Double by DoublePropertyDelegate(rectangle.arcWidthProperty())
    override var ry: Double by DoublePropertyDelegate(rectangle.arcHeightProperty())

}

class TransformNodeDelegate(val node:Node) : Transformable {

    class TransformFx(val node: Node) : Transform{
        override fun translate(x: Double, y: Double) {
            val transforms = node.transforms.filterIsInstance(javafx.scene.transform.Transform::class.java)
            node.transforms.removeAll(transforms)
            node.transforms.add(javafx.scene.transform.Transform.translate(x,y))
        }

    }

    override fun transform(init: Transform.() -> Unit) {
        TransformFx(node).apply(init)
    }

}

class StyleDelegate(val node: Node): StyledElement {
    override fun addClass(cssClass: CssClass) {
        node.styleClass.add(cssClass.name)
    }

}

class FillDelegate(val shape: jxShape): HasFill {
    override var fill: Color?
        get() = (shape.fill as javafx.scene.paint.Color?)?.d2vColor
        set(value) { shape.fill = value?.jfxColor}
}

class StrokeDelegate(val shape: jxShape): HasStroke {

    override var stroke: Color?
        get() = (shape.stroke as javafx.scene.paint.Color?)?.d2vColor
        set(value) { shape.stroke = value?.jfxColor}


    override var strokeWidth: Double?
        get() = shape.strokeWidth
        set(value) {if (value != null) shape.strokeWidth = value}

    init {
        stroke = colors.black
    }

}

class DoublePropertyDelegate(val property: DoubleProperty) {
    operator fun getValue(vizItem: VizItem, prop: KProperty<*>): Double = property.get()
    operator fun setValue(vizItem: VizItem, prop: KProperty<*>, d: Double) {
        property.set(d)
    }
}
