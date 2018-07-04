package com.caodd.pid.pid


class SimplifyIncPid(private val kp: Float, private val ki: Float, private val kd: Float) : Pid() {

    private val q1: Float = kp + ki + kd
    private val q2: Float = -(kp + 2 * kd)
    private val q3: Float = kd

    private fun calc(): Float {
        return q1 * ep0 + q2 * ep1 + q3 * ep2
    }

    override fun calcOut(): Float {
        val out = calc()
        if (out > 1f) return 1f
        if (out < -1f) return -1f
        return out
    }

    override fun toString(): String {
        return "SimplifyIncPid(kp=${kp.format("%.2f")}, ki=${ki.format("%.2f")}, kd=${kd.format("%.2f")})"
    }
}