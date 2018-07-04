package com.caodd.pid.pid


class SimplifyPositionPid(private val kp: Float, private val ki: Float, private val kd: Float) : Pid() {

    private fun calc(): Float {
        return kp * ep0 + ki * sum + kd * (ep0 - ep1)
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