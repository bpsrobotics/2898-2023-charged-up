@file:Suppress("FunctionName")  // capitalized function names

package com.bpsrobotics.engine.utils

object ColorSpaceConversions {
    /**
     * Converts an HSV color to an RGB color.
     *
     * @param h the h value [0-180]
     * @param s the s value [0-255]
     * @param v the v value [0-255]
     */
    fun HSVtoRGB(h: UByte, s: UByte, v: UByte): RGBA {
        if (s == 0.toUByte()) {
            return RGBA(v, v, v)
        }
        val region = h.toInt() / 30
        val remainder = (h.toInt() - region * 30) * 6
        val p = (v.toInt() * (255 - s.toInt()) shr 8).toUByte()
        val q = (v.toInt() * (255 - (s.toInt() * remainder shr 8)) shr 8).toUByte()
        val t = (v.toInt() * (255 - (s.toInt() * (255 - remainder) shr 8)) shr 8).toUByte()
        return when (region) {
            0 -> RGBA(v, t, p)
            1 -> RGBA(q, v, p)
            2 -> RGBA(p, v, t)
            3 -> RGBA(p, q, v)
            4 -> RGBA(t, p, v)
            else -> RGBA(v, p, q)
        }
    }

    /**
     * Converts an RGB color to an HSV color.
     *
     * @param r the h value [0-255]
     * @param g the s value [0-255]
     * @param b the v value [0-255]
     */
    fun RGBtoHSV(r: Int, g: Int, b: Int): IntArray {
        // R, G, B values are divided by 255
        // to change the range from 0..255 to 0..1
        val r = r / 255.0
        val g = g / 255.0
        val b = b / 255.0

        // h, s, v = hue, saturation, value
        val cmax = r.coerceAtLeast(g.coerceAtLeast(b)) // maximum of r, g, b
        val cmin = r.coerceAtMost(g.coerceAtMost(b)) // minimum of r, g, b
        val diff = cmax - cmin // diff of cmax and cmin.
        var h = -1.0

        // if cmax and cmax are equal then h = 0
        when (cmax) {
            cmin -> h = 0.0
            r -> h =
                (60 * ((g - b) / diff) + 360) % 360
            g -> h =
                (60 * ((b - r) / diff) + 120) % 360
            b -> h = (60 * ((r - g) / diff) + 240) % 360
        }

        // if cmax equal zero
        val s = if (cmax == 0.0) 0.0 else diff / cmax * 100

        // compute v
        val v = cmax * 100

        return intArrayOf(h.toInt(), s.toInt(), v.toInt())
    }
}
