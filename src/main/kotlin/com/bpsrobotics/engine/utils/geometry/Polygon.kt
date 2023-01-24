package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.DistanceUnit
import kotlin.math.PI

class Polygon(vararg val coordinates : Coordinate){
    val lines = mutableListOf<Line>()
    init {
        for ((p1, p2) in coordinates.toList().plus(coordinates.first()).zipWithNext()){
            lines.add(Line(p1,p2))
        }
    }
    fun contains(coordinate: Coordinate) : Boolean {
        var left = false
        var right = false
        for (i in lines){
            if(i.intersects(coordinate, 0.0)) left = true
            if (i.intersects(coordinate, PI)) right = true
            if (left && right) return true
        }
        return false
    }
    fun reflectHorizontally(x: DistanceUnit) : Polygon{
        val newCoordinates = coordinates.map { it.reflectHorizontally(x) }
        return Polygon(*newCoordinates.toTypedArray())
    }
}