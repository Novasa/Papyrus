package com.novasa.papyrus.core.util

import android.view.animation.Interpolator

/**
 * Created by Mikkel on 27-03-2016.
 */
object MathHelper {

    /**
     * Normalize v between min and max.
     * @param v v
     * @param min min
     * @param max max
     * @return a number between 0 and 1 (if v is inside that boundaries)
     */
    fun minMaxNormalize(v: Float, min: Float, max: Float): Float {
        return (v - min) / (max - min)
    }

    /**
     * Normalize v (0-1) between min and max. If v is outside the min-max boundaries, it will be clamped.
     * @param v v
     * @param min min
     * @param max max
     * @return a number between 0 and 1.
     */
    fun minMaxNormalizeClamped(v: Float, min: Float, max: Float): Float {
        if (v < min) return 0f
        return if (v > max) 1f else minMaxNormalize(v, min, max)
    }

    /**
     * Denormalize a normalized value between min and max.
     * @param v normalized value (0-1)
     * @param min min
     * @param max max
     * @return denormalized x.
     */
    fun minMaxDenormalize(v: Float, min: Float, max: Float): Float {
        return v * (max - min) + min
    }

    /**
     * Denormalize a normalized value between min and max. If v is outside [0-1] range, min or max will be returned.
     * @param v normalized value (0-1)
     * @param min min
     * @param max max
     * @return denormalized x.
     */
    fun minMaxDenormalizeClamped(v: Float, min: Float, max: Float): Float {
        if (v < 0f) return min
        return if (v > 1f) max else minMaxDenormalize(v, min, max)
    }

    /**
     * Renormalizes a value from one range to another.
     * This is just a [.minMaxNormalize] relative to `min0` and `max0`,
     * followed by a [.minMaxDenormalize] relative to `min1` and `max1`.
     * @param v0 The original value relative to `min0` and `max0`
     * @param min0 original range min
     * @param max0 original range max
     * @param min1 new range min
     * @param max1 new range max
     * @param interpolator the normalized value is interpolated, or null for linear interpolation
     * @return the renormalized value, relative to `min1` and `max1`
     */
    fun renormalize(v0: Float, min0: Float, max0: Float, min1: Float, max1: Float, interpolator: Interpolator?): Float {
        var normalized = minMaxNormalize(v0, min0, max0)
        if (interpolator != null) normalized = interpolator.getInterpolation(normalized)
        return minMaxDenormalize(normalized, min1, max1)
    }

    /**
     * Renormalizes a value from one range to another.
     * This is just a [.minMaxNormalize] relative to `min0` and `max0`,
     * followed by a [.minMaxDenormalize] relative to `min1` and `max1`.
     * If v is outside the min-max boundaries, it will be clamped.
     * @param v0 The original value relative to `min0` and `max0`
     * @param min0 original range min
     * @param max0 original range max
     * @param min1 new range min
     * @param max1 new range max
     * @param interpolator the normalized value is interpolated
     * @return the renormalized value, relative to `min1` and `max1`
     */
    fun renormalizeClamped(
        v0: Float,
        min0: Float,
        max0: Float,
        min1: Float,
        max1: Float,
        interpolator: Interpolator?
    ): Float {
        var normalized = minMaxNormalizeClamped(v0, min0, max0)
        if (interpolator != null) normalized = interpolator.getInterpolation(normalized)
        return minMaxDenormalize(normalized, min1, max1)
    }
}
