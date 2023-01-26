package com.bpsrobotics.engine.utils.geometry

import com.bpsrobotics.engine.utils.DistanceUnit
import com.bpsrobotics.engine.utils.Sugar.eqEpsilon
import edu.wpi.first.math.geometry.Pose2d
import kotlin.math.*

class Line(val point1 : Coordinate, val point2 : Coordinate){
    fun intersects(coordinate : Coordinate, theta : Double) : Boolean{
        val theta1 = atan2(point1.y-coordinate.y, point1.x-coordinate.x)
        val theta2 = atan2(point2.y-coordinate.y, point2.x-coordinate.x)
        var dtheta = theta2-theta1
        var ntheta=theta-theta1
        dtheta= atan2(sin(dtheta), cos(dtheta))
        ntheta= atan2(sin(ntheta), cos(ntheta))
        return (sign(ntheta) == sign(dtheta) && abs(ntheta) <= abs(dtheta))
    }
    /**
     * Gets the intersection of a ray-cast and the line
     * @param coordinate Origin of the ray-cast
     * @param theta Rotation of the ray-cast given in radians
     * @return Intersection point of ray-cast and line
     * @sample intersection
     * */
    fun intersection(coordinate : Coordinate, theta : Double) : Coordinate? {
        val returnCoord = Coordinate(0.0,0.0)
        val xPosition : Double
        val yPosition : Double
        if(!intersects(coordinate, theta)) return null
        println(cos(theta) eqEpsilon 0)
        if(point2.x-point1.x == 0.0 || cos(theta) eqEpsilon 0){
            val cotOfTheta = cos(theta) / sin(theta)
            var slope = (point2.x-point1.x)/(point2.y-point1.y)
            if(slope.isInfinite()) {
                yPosition=point1.y
            }
            else {
                yPosition=(point1.x-slope*point1.y-coordinate.x + cotOfTheta*coordinate.y)/(cotOfTheta-slope)
            }
            xPosition =cotOfTheta*yPosition+coordinate.x-cotOfTheta*coordinate.y
        }else{
            val rm = sin(theta) / cos(theta)
            val lm = (point2.y-point1.y)/(point2.x-point1.x)
            xPosition=(point1.y-lm*point1.x-coordinate.y+rm*coordinate.x)/(rm-lm)
            yPosition=rm*xPosition+coordinate.y-rm*coordinate.x
        }
        return Coordinate(xPosition, yPosition)
    }
    /**
     * Gets the intersection of a ray-cast and the line
     * @param pose Pose of the ray-cast
     * @return Intersection point of ray-cast and line
     * @sample intersection
     * */
    fun intersection(pose: Pose2d) : Coordinate? {
        return intersection(Coordinate(pose.x,pose.y), pose.rotation.radians)
    }
    /**
     * Gets the distance from the intersection of a ray-cast and the line
     * @param coordinate Origin of the ray-cast
     * @param rotation Rotation of the raycast
     * @return Distance from the intersection point of ray-cast and line
     * @sample distance
     * */
    fun distance(coordinate : Coordinate, rotation : Double) : Double? {
        val intersectionPoint = intersection(coordinate, rotation) ?: return null
        return (coordinate - intersectionPoint).magnitude
    }
    /**
     * Gets the distance from the intersection of a ray-cast and the line
     * @param pose Pose of the raycast
     * @return Distance from the intersection point of ray-cast and line
     * @sample distance
     * */
    fun distance(pose : Pose2d) : Double? {
        val intersectionPoint = intersection(pose) ?: return null
        return (Coordinate(pose.x,pose.y) - intersectionPoint).magnitude
    }
    /**
     * Returns the line reflected over a vertical line at the given x coordinate
     * @param x X value the relection line
     * @return Reflected line
     * @author Ozy King
     */
    fun reflectHorizontally(x: DistanceUnit) : Line {
        return Line(point1.reflectHorizontally(x),point2.reflectHorizontally(x))
    }
}