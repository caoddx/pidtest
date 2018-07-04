package com.caodd.pid.pid

class IncPid(val kp: Float, val ti: Float, val td: Float) : Pid() {

    private val q1: Float = kp * (1 + samplingPeriod / ti + td / samplingPeriod)
    private val q2: Float = -kp * (1 + 2 * td / samplingPeriod)
    private val q3: Float = kp * td / samplingPeriod

    override fun calcOut(): Float {
        return q1 * ep0 + q2 * ep1 + q3 * ep2
    }

    /*override fun toString(): String {
        return "q1 = ${q1.format("%.2f")} q2 = ${q2.format("%.2f")} q3 = ${q3.format("%.2f")}"
    }*/

    override fun toString(): String {
        return "IncPid(kp=${kp.format("%.2f")}, ti=${ti.format("%.2f")}, td=${td.format("%.2f")})"
    }

    companion object {
        private const val samplingPeriod = 1f
    }
}