package com.example.gridtesttask

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val COUNT_OF_PARTICIPANT = 7
private var EditTextMatrix = mutableListOf<MutableList<EditText>>()
private lateinit var viewModel: AppViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        val scrollView = ScrollView(applicationContext)
        val linearLayout = LinearLayout(applicationContext)
        linearLayout.orientation = LinearLayout.HORIZONTAL

        linearLayout.addView(createTable(this))

        scrollView.addView(linearLayout)
        setContentView(scrollView)
        viewModel.initTable(COUNT_OF_PARTICIPANT)
        setKeyListener(this)
    }
}

private fun createTable(act: AppCompatActivity): TableLayout {

    val tableLayout = TableLayout(act)
    var tableRow: TableRow
    var textView: TextView
    var editText: EditText

    tableLayout.addView(createHeadRow(act))

    for (row in 1..COUNT_OF_PARTICIPANT) {
        EditTextMatrix.add(mutableListOf())

        tableRow = TableRow(act)

        textView = TextView(act)
        textView.text = act.resources.getString(R.string.participant, row);
        textView.setBackgroundResource(R.drawable.shape_table)
        tableRow.addView(
            textView, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT
            )
        )

        textView = TextView(act)
        textView.text = "$row"
        textView.setBackgroundResource(R.drawable.shape_table)
        tableRow.addView(
            textView, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT
            )
        )

        for (col in 1..COUNT_OF_PARTICIPANT) {
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = LengthFilter(1)

            editText = EditText(act)

            if (col == row) {
                editText.setBackgroundResource(R.drawable.empty_grid)
                editText.isFocusable = false
                editText.isEnabled = false
                tableRow.addView(editText)
                continue
            }

            EditTextMatrix[row - 1].add(editText)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = fArray
            editText.setBackgroundResource(R.drawable.shape_table)
            tableRow.addView(editText)
        }

        textView = TextView(act)
        val currentTextViewSum = View.generateViewId()
        textView.id = currentTextViewSum
        textView.setBackgroundResource(R.drawable.shape_table)
        tableRow.addView(
            textView, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT
            )
        )
        viewModel.resultsColumn.observe(act) {
            if (it[row - 1] != null) {
                act.findViewById<TextView>(currentTextViewSum).text = it[row - 1].toString()
            }
        }

        textView = TextView(act)
        val currentTextViewPlace = View.generateViewId()
        textView.id = currentTextViewPlace
        textView.setBackgroundResource(R.drawable.shape_table)
        tableRow.addView(
            textView, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT
            )
        )
        viewModel.leaders.observe(act) {
            for (pair in it) {
                if (pair.first == row - 1) {
                    act.findViewById<TextView>(currentTextViewPlace).text = pair.second.toString()
                    break
                }
            }
        }

        tableLayout.addView(tableRow)
    }
    return tableLayout
}

private fun createHeadRow(context: Context): TableRow {

    val tableRow = TableRow(context)

    var textView = TextView(context)
    textView.setBackgroundResource(R.drawable.shape_table)
    tableRow.addView(textView)

    textView = TextView(context)
    textView.setBackgroundResource(R.drawable.shape_table)
    tableRow.addView(textView)

    for (i in 1..COUNT_OF_PARTICIPANT) {
        textView = TextView(context)
        textView.text = "$i"
        textView.setBackgroundResource(R.drawable.shape_table)
        tableRow.addView(textView)
    }

    textView = TextView(context)
    textView.setText(R.string.point_sum)
    textView.setBackgroundResource(R.drawable.shape_table)
    tableRow.addView(textView)

    textView = TextView(context)
    textView.setText(R.string.place)
    textView.setBackgroundResource(R.drawable.shape_table)

    tableRow.addView(textView)

    return tableRow
}

private fun setKeyListener(activity: AppCompatActivity) {
    for (row in 0 until EditTextMatrix.size) {
        for (col in 0 until EditTextMatrix[row].size) {

            EditTextMatrix[row][col].setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

                    if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action != KeyEvent.ACTION_DOWN) {

                        try {
                            EditTextMatrix[row][col].setTextColor(activity.resources.getColor(R.color.black))
                            val temp = EditTextMatrix[row][col].text.toString().toInt()
                            if (temp !in 0..5) {
                                EditTextMatrix[row][col].setTextColor(activity.resources.getColor(R.color.red))
                                Toast.makeText(
                                    activity,
                                    activity.resources.getText(R.string.incorrect_input),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                if (col == EditTextMatrix[row].size - 1 && row == EditTextMatrix.size - 1) {
                                    EditTextMatrix[0][0].requestFocus()
                                } else if (col == EditTextMatrix[row].size - 1) {
                                    EditTextMatrix[row + 1][0].requestFocus()
                                } else {
                                    EditTextMatrix[row][col + 1].requestFocus()
                                }


                                viewModel.writePoint(
                                    row,
                                    col,
                                    EditTextMatrix[row][col].text.toString().toInt()
                                )
                            }

                        } catch (e: java.lang.Exception) {
                            EditTextMatrix[row][col].setTextColor(activity.resources.getColor(R.color.red))
                            Toast.makeText(
                                activity,
                                activity.resources.getText(R.string.incorrect_input),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        return true
                    }
                    return false
                }
            })
        }
    }
}