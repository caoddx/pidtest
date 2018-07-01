package com.caodd.pid

class Pid(kp: Float, ti: Float, td: Float) {

    private val q1: Float
    private val q2: Float
    private val q3: Float

    private var ep0: Float = 0.0f
    private var ep1: Float = 0.0f
    private var ep2: Float = 0.0f

    init {
        q1 = kp * (1 + samplingPeriod / ti + td / samplingPeriod)
        q2 = -kp * (1 + 2 * td / samplingPeriod)
        q3 = kp * td / samplingPeriod
    }

    fun updateE(e: Float) {
        ep2 = ep1
        ep1 = ep0
        ep0 = e
    }

    fun getDelta(): Float {
        return q1 * ep0 + q2 * ep1 + q3 * ep2
    }

    override fun toString(): String {
        return "q1 = ${q1.format("%.2f")} q2 = ${q2.format("%.2f")} q3 = ${q3.format("%.2f")}"
    }

    private fun Any.format(s: String): String {
        return String.format(s, this)
    }

    companion object {
        private const val samplingPeriod = 1f
    }
}