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
data class Rectangle(val coordinate1: Coordinate, val coordinate2: Coordinate) {
    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this(Coordinate(x1, y1), Coordinate(x2, y2))

    val x1 get() = coordinate1.x
    val y1 get() = coordinate1.y
    val x2 get() = coordinate2.x
    val y2 get() = coordinate2.y
    val center get() = coordinate1 + ((coordinate1 - coordinate2)/2.0)
    override fun toString(): String {
        return "Rect: {Top left: ${coordinate1}, Bottom right: ${coordinate2}}"
    }
    /** Returns "other" as a coordinate if center of rectangle is the origin (Use .magnitude to get the true distance)
     * @param other Coordinate to find distance from
     * @return "Other's" distance to the center
     * @author Ozy
     * */
    fun distToCenter(other: Coordinate) : Coordinate{
        return other - center
    }
    /** Returns "other" as a coordinate if center of rectangle is the origin (Use .magnitude to get the true distance)
     * @param other Pose to find distance from
     * @return "Other's" distance to the center
     * @author Ozy
     * */
    fun distToCenter(other: Pose2d) : Coordinate{
        return Coordinate(other.x - center.x, other.y - center.y)
    }
    /** Returns true if the coordinate given is within the vertical projection of the rectangle
     * @param x X position to check
     * @return If point is in vertical projection of the rectangle
     * @author Ozy
     * */
    fun containsX(x: Double): Boolean {
        return x in coordinate1.x..coordinate2.x
    }
    /** Returns true if the coordinate given is within the vertical projection of the rectangle
     * @param coordinate Coordinate to check
     * @return If point is in vertical projection of the rectangle
     * @author Ozy
     * */
    fun containsX(coordinate: Coordinate): Boolean {
        return containsX(coordinate.x)
    }
    /** Returns true if the coordinate given is within the vertical projection of the rectangle
     * @param pose Pose to check
     * @return If Pose is in vertical projection of the rectangle
     * @author Ozy
     * */
    fun containsX(pose: Pose2d): Boolean {
        return containsX(pose.x)
    }
    /** Returns true if the coordinate given is within the horizontal projection of the rectangle
     * @param y Y coordinate to check
     * @return If point is in the horizontal projection of the rectangle
     * @author Ozy
     * */
    fun containsY(y: Double): Boolean {
        return y in coordinate2.y..coordinate1.y
    }
    /** Returns true if the coordinate given is within the horizontal projection of the rectangle
     * @param coordinate Coordinate to check
     * @return If point is in the horizontal projection of the rectangle
     * @author Ozy
     * */
    fun containsY(coordinate: Coordinate): Boolean {
        return containsY(coordinate.y)
    }
    /** Returns true if the pose given is within the horizontal projection of the rectangle
     * @param pose Pose to check
     * @return If Pose is in the horizontal projection of the rectangle
     * @author Ozy
     * */
    fun containsY(pose: Pose2d): Boolean {
        return containsY(pose.y)
    }
    /** Returns true if the given x and y position are within the bounds of the rectangle
     * @param x X Coordinate to check
     * @param y Y Coordinate to check
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
