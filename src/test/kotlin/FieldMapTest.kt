import com.bpsrobotics.engine.utils.ft
import com.bpsrobotics.engine.utils.geometry.Coordinate
import com.bpsrobotics.engine.utils.geometry.Line
import com.bpsrobotics.engine.utils.geometry.Polygon
import com.bpsrobotics.engine.utils.geometry.Rectangle
import com.team2898.robot.blueTeam
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.PI

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
    /*@Test
    fun testRectangle(){
        val newRectangle = Rectangle(
            Coordinate((-1).ft,1)
        )
    }*/
    @Test
    fun testIntersection(){
        val newLine = Line(
            Coordinate(0.ft,0.ft),
            Coordinate(2.ft,0.ft)
        )
        var angle = 0.0
        for(i in -PI..PI step 0.1){
            val intersection = newLine.intersection(Coordinate(1.ft,1.ft),i)
            println("Angle: ${i}, Intersects: ${intersection}")
        }

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
