package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.DistanceUnit
import kotlin.math.PI

/**
 * A two-dimensional shape with at least 3 sides. (Sides cannot intersect)
 * @property coordinates List of polygon vertices of the polygon
 * @author Ozy King
 */
class Polygon(vararg val coordinates : Coordinate){
    val lines = mutableListOf<Line>()
    init {
        for ((p1, p2) in coordinates.toList().plus(coordinates.first()).zipWithNext()){
            lines.add(Line(p1,p2))
        }
    }

    /**
     * Returns true if the coordinate is within the bounds of the polygon
     * @param coordinate Coordinate to check
     * @return If point is in polygon
     * @author Ozy King
     */
    fun contains(coordinate: Coordinate) : Boolean {
        var intersections = 0
        for (i in lines){
            //println(i.intersects(coordinate, 0.0))
            if (i.intersects(coordinate, PI)) intersections++
            if (intersections > 1) return false
        }
        return intersections == 1
    }
    /**
     * Returns true if the point is within the bounds of the polygon
     * @param x X value of the point to check
     * @param y Y value of the point to check
     * @return If point is in polygon
     * @author Ozy King
     */
    fun contains(x: Double, y: Double) : Boolean{
        return contains(Coordinate(x,y))
    }
    /**
     * Returns a polygon reflected over the x value given
     * @param x X value of the vertical line to reflect over
     * @return Reflected polygon
     * @author Ozy King
     */
    fun reflectHorizontally(x: DistanceUnit) : Polygon{
        val newCoordinates = coordinates.map { it.reflectHorizontally(x) }
        return Polygon(*newCoordinates.toTypedArray())
    }
}