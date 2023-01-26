import com.bpsrobotics.engine.utils.Sugar.eqEpsilon
import com.bpsrobotics.engine.utils.ft
import com.bpsrobotics.engine.utils.geometry.Coordinate
import com.bpsrobotics.engine.utils.geometry.Line
import com.bpsrobotics.engine.utils.geometry.Polygon
import com.bpsrobotics.engine.utils.geometry.Rectangle
import com.team2898.robot.blueTeam
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.round
import kotlin.math.roundToInt

class FieldMapTest {
    @Test
    fun testPolygon(){
        val newPoly = Polygon(
            Coordinate(0.ft,0.ft),
            Coordinate(2.ft,0.ft),
            Coordinate(2.ft,2.ft),
            Coordinate(0.ft,2.ft)
        )
        Assertions.assertTrue(newPoly.contains(Coordinate(1.0,1.0)))
        Assertions.assertFalse(newPoly.contains(Coordinate((-1).ft,1.ft)))
    }
    @Test
    fun testPolygonReflection(){
        val newPoly = Polygon(
            Coordinate(0.ft,0.ft),
            Coordinate(2.ft,0.ft),
            Coordinate(2.ft,2.ft),
            Coordinate(0.ft,2.ft)
        )
        Assertions.assertTrue(newPoly.contains(Coordinate(1.0,1.0)))
        Assertions.assertFalse(newPoly.contains(Coordinate((-1).ft,1.ft)))
    }
    @Test
    fun testRectangle(){
        val newRectangle = Rectangle(
            Coordinate(0.ft,2.ft),
            Coordinate(2.ft, 0.ft)
        )
        Assertions.assertTrue(newRectangle.contains(1.0,1.0))
    }
    @Test
    fun testRectangleReflection(){
        val newRectangle = Rectangle(
            Coordinate((-2).ft,2.ft),
            Coordinate(0.ft, (-2).ft)
        ).reflectHorizontally(0.ft)
        Assertions.assertTrue(newRectangle.contains(1.0,1.0))
    }


    fun assertEpsilon(expected: Double, actual: Double, epsilon: Double = 0.01) {
        if (!(actual eqEpsilon expected)) {
            Assertions.fail<Double>("Assertion failed! expected: $expected found: $actual")
        }
        Assertions.assertTrue((expected - actual).absoluteValue < epsilon)
    }

    @Test
    fun testIntersection(){
        val newLine = Line(
            Coordinate(0.ft,0.ft),
            Coordinate(2.ft,0.ft)
        )

        val intersection = newLine.intersection(Coordinate(1.ft,1.ft),-PI/2) ?: run {Assertions.fail<Nothing>("Ono bad"); return }
        //println(intersection)
        assertEpsilon(1.0, intersection.x)
        assertEpsilon(0.0, intersection.y)

        val newLine2 = Line(
            Coordinate(0.ft,0.ft),
            Coordinate(0.ft,2.ft)
        )


        val intersection2 = newLine2.intersection(Coordinate(1.ft,1.ft),-PI) ?: run {Assertions.fail<Nothing>(); return }
        println(intersection2)
        assertEpsilon(0.0, intersection2.x)
        assertEpsilon(1.0, intersection2.y)

    }

}

private infix fun ClosedFloatingPointRange<Double>.step(s: Double) = object : Iterator<Double> {
        var c = 0

        override fun hasNext(): Boolean {
            return s * (c + 1) + start <= endInclusive
        }

        override fun next(): Double {
            return s * c++ + start
        }
    }
