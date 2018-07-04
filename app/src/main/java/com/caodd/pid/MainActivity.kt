package com.caodd.pid

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.caodd.pid.pid.Pid
import com.caodd.pid.pid.SimplifyPositionPid
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
                "valves" to Color.BLUE,
                "outs" to Color.GREEN,
                "sums" to Color.BLACK
        )

        button.setOnClickListener {
            val kp = editKp.text.toString().toFloat()
            val ti = editTi.text.toString().toFloat()
            val td = editTd.text.toString().toFloat()

            // 10 8 1
            val pid = SimplifyPositionPid(kp, ti, td)

            val map = update(pid, 1f, 0f, 0.5f) { current, valve ->
                current + valve * 0.1f
            }

            val setMap = map.mapValues { (name, list) ->
                makeSet(name, list, colors[name]!!)
            }.filter { it.key != "sums" }

            /*val lineData = if (lastSet != null) {
                lastSet?.color = Color.GRAY
                lastSet?.label = "last histories"
                LineData(setMap.values + lastSet)
            } else {
                LineData(setMap.values.toList())
            }*/

            val lineData = LineData(setMap.values.toList())

            //lastSet = setMap["histories"]

            lineChart.data = lineData
            lineChart.animateX(1000)


        }
    }

    private fun update(pid: Pid, vararg targets: Float, applyValve: (current: Float, valve: Float) -> Float): Map<String, List<Float>> {

        val histories = mutableListOf<Float>()
        val valves = mutableListOf<Float>()
        val outs = mutableListOf<Float>()
        val sums = mutableListOf<Float>()

        var current = 0.0f
        var valve = 0.0f
        var out = 0f

        val upList: () -> Unit = {
            histories.add(current)
            valves.add(valve)
            outs.add(out)
            sums.add(pid.sum)
        }

        for (i in 0..19) {
            pid.updateError(0f)
            out = pid.getOut()
            valve += out
            upList()
        }

        targets.forEach {
            val target = it

            for (i in 0..499) {

                pid.updateError(target - current)
                out = pid.getOut()

                valve = out

                if (valve > 1.0f) valve = 1.0f
                if (valve < -1.0f) valve = -1.0f

                current = applyValve(current, valve)

                upList()
            }
        }

        return mapOf(
                "histories" to histories.toList(),
                "valves" to valves.toList(),
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
