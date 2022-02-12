@file:Suppress("FunctionName")  // capitalized function names

package com.bpsrobotics.engine.utils

object ColorSpaceConversions {
    /**
     * Converts an HSV color to an RGB color.
     */
    fun HSVtoRGB(hsv: HSVA): RGBA {
        val h = hsv.h.toInt()
        val s = hsv.s.toInt()
        val v = hsv.v.toInt()
        if (s == 0) {
            return RGBA(v, v, v)
        }
        val region = h / 30
        val remainder = (h - region * 30) * 6
        val p = v * (255 - s) shr 8
        val q = v * (255 - (s * remainder shr 8)) shr 8
        val t = v * (255 - (s * (255 - remainder) shr 8)) shr 8
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
     */
    fun RGBtoHSV(rgb: RGBA): HSVA {
        // R, G, B values are divided by 255
        // to change the range from 0..255 to 0..1
        val r = rgb.r.toDouble() / 255
        val g = rgb.g.toDouble() / 255
        val b = rgb.b.toDouble() / 255

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

        return HSVA(h.toInt(), s.toInt().toUByte(), v.toInt().toUByte())
    }
}
