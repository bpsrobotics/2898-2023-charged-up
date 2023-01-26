package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.DistanceUnit
import com.bpsrobotics.engine.utils.Meters
import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.*

/**
 * A two-dimensional shape with 2 sets of parallel lines
 * @property coordinate1 Top left corner of the rectangle
 * @property coordinate2 Bottom right corner of the rectangle
 * @author Anthony, Ozy
 */
class Rectangle(val coordinate1: Coordinate, val coordinate2: Coordinate) {
    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this(Coordinate(x1, y1), Coordinate(x2, y2))

    val x1 get() = coordinate1.x
    val y1 get() = coordinate1.y
    val x2 get() = coordinate2.x
    val y2 get() = coordinate2.y
    override fun toString(): String {
        return "Rect: {Top left: ${coordinate1}, Bottom right: ${coordinate2}}"
    }
    /** Returns true if the point given is within the bounds of the rectangle
     * @param x X value of point
     * @param y Y value of point
     * @return If point in rectangle
     * @author Anthony, Ozy
     * */
    fun contains(x: Double, y: Double): Boolean {
        return x in coordinate1.x..coordinate2.x && y in coordinate2.y..coordinate1.y
    }
    /** Returns true if the coordinate given is within the bounds of the rectangle
     * @param coordinate Coordinate to check
     * @return If point in rectangle
     * @author Anthony, Ozy
     * */
    fun contains(coordinate: Coordinate): Boolean {
        return contains(coordinate.x,coordinate.y)
    }
    /** Returns true if the pose given is within the bounds of the rectangle
     * @param pose Pose to check
     * @return If Pose in rectangle
     * @author Anthony
     * */
    operator fun contains(pose: Pose2d): Boolean {
        return contains(pose.x, pose.y)
    }
    fun reflectHorizontally(x: DistanceUnit) : Rectangle{
        val coor1 = coordinate1.reflectHorizontally(x)
        val coor2 = coordinate2.reflectHorizontally(x)
        val center = Meters((coor1.x+coor2.x)/2)
        return Rectangle(
            coor1.reflectHorizontally(center),
            coor2.reflectHorizontally(center)
        )
    }
}
