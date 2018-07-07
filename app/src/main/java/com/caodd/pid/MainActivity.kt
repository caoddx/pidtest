package com.caodd.pid

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import java.util.*

class MainActivity : AppCompatActivity() {
    private val colors = mapOf(
            "histories" to Color.RED,
            "valves" to Color.BLUE,
            "outs" to Color.GREEN,
            "sums" to Color.BLACK,
            "targets" to Color.MAGENTA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        var plus = editTextPlus.text.toString().toFloat()
        Handler().postDelayed({
            plus = editTextPlus.text.toString().toFloat()
        }, 50)

        buttonMinus.setOnClickListener {
            plus -= 0.1f
            if (plus < 0f) plus = 0f
            editTextPlus.setText(String.format("%.2f", plus))
        }
        buttonPlus.setOnClickListener {
            plus += 0.1f
            editTextPlus.setText(String.format("%.2f", plus))
        }
        buttonKpPlus.setOnClickListener {
            val f = editKp.text.toString().toFloat()
            editKp.setText((f + plus).toString())
        }
        buttonKpMinus.setOnClickListener {
            val f = editKp.text.toString().toFloat()
            editKp.setText((f - plus).toString())
        }
        buttonKiPlus.setOnClickListener {
            val f = editKi.text.toString().toFloat()
            editKi.setText((f + plus).toString())
        }
        buttonKiMinus.setOnClickListener {
            val f = editKi.text.toString().toFloat()
            editKi.setText((f - plus).toString())
        }
        buttonKdPlus.setOnClickListener {
            val f = editKd.text.toString().toFloat()
            editKd.setText((f + plus).toString())
        }
        buttonKdMinus.setOnClickListener {
            val f = editKd.text.toString().toFloat()
            editKd.setText((f - plus).toString())
        }

        var lastSet: LineDataSet? = null

        button.setOnClickListener {
            val kp = editKp.text.toString().toFloat()
            val ti = editKi.text.toString().toFloat()
            val td = editKd.text.toString().toFloat()

            val pid: Pid = SimplifyPositionPid(kp, ti, td)

            val setMap = updateMap(pid)
            val set = setMap["histories"]!!
            set.label = pid.toString().filter { it.isLetter().not() && it != '=' }
            val lineData = LineData(setMap.values.toList())

            lastSet?.let {
                it.color = 0xD0888888.toInt()
                lineData.addDataSet(it)
            }
            lastSet = set

            lineChart.data = lineData
            lineChart.animateX(1000)
        }
    }

    private fun updateMap(pid: Pid): Map<String, LineDataSet> {
        val vs = LinkedList<Float>()

        for (i in 0..19) {
            //vs.offer(0.5f)
        }

        val map = update(pid, 1f, 1f, 1f) { current, valve ->
            vs.offer(valve - 0.2f)
            // current + (vs.poll() - 0.5f) * 0.1f
            current + (vs.poll() - current) * 0.1f
        }

        return map.mapValues { (name, list) ->
            makeSet(name, list, colors[name]!!)
        }
                //.filter { it.key != "sums" }
    }

    private fun update(pid: Pid, vararg targets: Float, applyValve: (current: Float, valve: Float) -> Float): Map<String, List<Float>> {

        val histories = mutableListOf<Float>()
        val valves = mutableListOf<Float>()
        val outs = mutableListOf<Float>()
        val sums = mutableListOf<Float>()
        val tgs = mutableListOf<Float>()

        var current = 0.0f
        var valve: Float
        var out: Float

        (floatArrayOf(current) + targets).forEachIndexed { index, target ->

            if (index == 0) {
                0..49
            } else {
                0..299
            }.forEach {
                pid.updateError(target - current)
                out = pid.getOut()
                valve = (out + 1) / 2
                current = applyValve(current, valve)

                histories.add(current)
                valves.add(valve)
                outs.add(out)
                sums.add(pid.sum)
                tgs.add(target)
            }

        }

        return mapOf(
                "targets" to tgs,
                "sums" to sums.toList(),
                "valves" to valves.toList(),
                "outs" to outs.toList(),
                "histories" to histories.toList()
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
