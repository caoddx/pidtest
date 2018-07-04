package com.caodd.pid.pid

abstract class Pid {

    var sum: Float = 0f
        private set

    protected var ep0: Float = 0f
        private set
    protected var ep1: Float = 0f
        private set
    protected var ep2: Float = 0f
        private set

    /**
     * @param e -1.0 到 +1.0 之间
     */
    fun updateError(e: Float) {
        // require(e <= 1f)
        // require(e >= -1f)

        ep2 = ep1
        ep1 = ep0
        ep0 = e

        sum += e
    }

    abstract fun calcOut(): Float

    /**
     * @return -1.0 到 +1.0 之间
     */
    fun getOut(): Float {
        val out = calcOut()
        if (out > 1f) return 1f
        if (out < -1f) return -1f
        return out
    }

    protected fun Any.format(s: String): String {
        return String.format(s, this)
    }
}