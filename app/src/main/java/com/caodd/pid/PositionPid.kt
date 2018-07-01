package com.caodd.pid


class PositionPid(private val kp: Float, private val ki: Float, private val kd: Float) {

    var sum: Float = 0f
        private set

    private var ep0: Float = 0f
    private var ep1: Float = 0f
    private var ep2: Float = 0f

    fun updateError(e: Float) {
        ep2 = ep1
        ep1 = ep0
        ep0 = e

        sum += e
    }

    private fun calc(): Float {
        return kp * ep0 + ki * sum + kd * (ep0 - ep1)
    }

    /**
     * @return -1.0 到 +1.0 之间
     */
    fun getOut(): Float {
        val out = calc()
        if (out > 1f) return 1f
        if (out < -1f) return -1f
        return out
    }

    override fun toString(): String {
        return "kp = ${kp.format("%.2f")} ki = ${ki.format("%.2f")} kd = ${kd.format("%.2f")}"
    }

    private fun Any.format(s: String): String {
        return String.format(s, this)
    }
}