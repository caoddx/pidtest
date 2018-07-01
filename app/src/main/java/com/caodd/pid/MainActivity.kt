package com.caodd.pid

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var lastSet: LineDataSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        val colors = mapOf(
                "histories" to Color.RED,
                "deltas" to Color.BLUE,
                "outs" to Color.GREEN,
                "sums" to Color.BLACK
        )

        button.setOnClickListener {
            val map = update()

            val setMap = map.mapValues { (name, list) -> makeSet(name, list, colors[name]!!) }.filter { it.key != "sums" }

            val lineData = if (lastSet != null) {
                lastSet?.color = Color.GRAY
                lastSet?.label = "last histories"
                LineData(setMap.values + lastSet)
            } else {
                LineData(setMap.values.toList())
            }

            lastSet = setMap["histories"]

            lineChart.data = lineData
            lineChart.animateX(1000)
        }
    }

    private fun update(): Map<String, List<Float>> {
        val kp = editKp.text.toString().toFloat()
        val ti = editTi.text.toString().toFloat()
        val td = editTd.text.toString().toFloat()

        val histories = mutableListOf<Float>()
        val deltas = mutableListOf<Float>()
        val outs = mutableListOf<Float>()
        val sums = mutableListOf<Float>()


        // 10 8 1
        val pid = PositionPid(kp, ti, td)

        textView.text = pid.toString()

        var target = 1.0f
        var current = 0.0f
        var out = 0.0f
        var delta = 0f

        val upList: () -> Unit = {
            histories.add(current)
            deltas.add(delta)
            outs.add(out)
            sums.add(pid.sum)
        }

        for (i in 0..19) {
            pid.updateError(0f)
            val delta = pid.getOut()
            out += delta

            upList()
        }

        for (i in 0..499) {
            val e = target - current
            pid.updateError(e)
            val delta = pid.getOut()

            out = delta

            if (out > 1.0f) out = 1.0f
            if (out < -1.0f) out = -1.0f

            current += out * 0.1f

            upList()
        }

        /*target = 0f

        for (i in 0..199) {
            val e = target - current
            pid.updateError(e)
            var d = pid.getOut()

            if (d > 1.0f) d = 1.0f
            if (d < -1.0f) d = -1.0f

            delta += d

            if (delta > 1.0f) delta = 1.0f
            if (delta < -1.0f) delta = -1.0f

            out += delta

            if (out > 1.0f) out = 1.0f
            if (out < -1.0f) out = -1.0f

            current += out * 0.01f

            histories.add(current)
            deltas.add(d)
            outs.add(out)
        }*/

        return mapOf(
                "histories" to histories.toList(),
                "deltas" to deltas.toList(),
                "outs" to outs.toList(),
                "sums" to sums.toList()
        )
    }

    private fun makeSet(label: String, list: List<Float>, color: Int): LineDataSet {
        val entries = list.mapIndexed { index, fl -> Entry(index.toFloat(), fl) }
        val set = LineDataSet(entries, label)
        set.setDrawCircles(false)
        set.color = color
        return set
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
