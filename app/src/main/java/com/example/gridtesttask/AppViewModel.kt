package com.example.gridtesttask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    private val _resultsColumn = MutableLiveData<MutableList<Int?>>()
    val resultsColumn: LiveData<MutableList<Int?>> = _resultsColumn

    private val _leaders = MutableLiveData<MutableList<Pair<Int, Int>>>()
    val leaders: LiveData<MutableList<Pair<Int, Int>>> = _leaders

    private val resultsTable = mutableListOf<MutableList<Int?>>()

    fun initTable(countOfParticipant: Int) {
        _resultsColumn.value = mutableListOf()
        _leaders.value = mutableListOf()

        for (i in 0 until countOfParticipant) {
            _resultsColumn.value!!.add(null)
            resultsTable.add(mutableListOf())
            for (j in 0 until countOfParticipant - 1) {
                resultsTable[i].add(null)
            }
        }
    }

    fun writePoint(row: Int, col: Int, value: Int) {
        resultsTable[row][col] = value

        if (rowIsFull(resultsTable[row])) {
            _resultsColumn.value?.set(row, sumOfRow(resultsTable[row]))
            _resultsColumn.postValue(_resultsColumn.value)

            if (allRowsFull()) {
                setLeaders(_resultsColumn.value!!)
            }
        }

    }

    private fun rowIsFull(row: List<Int?>): Boolean {
        var rowIsFull = true

        for (element in row) {
            if (element == null) rowIsFull = false
        }
        return rowIsFull
    }

    private fun sumOfRow(row: List<Int?>): Int {
        var sum = 0

        for (element in row) {
            sum += element!!
        }
        return sum
    }

    private fun allRowsFull(): Boolean {
        var allRowsFull = true

        for (element in _resultsColumn.value!!) {
            if (element == null) allRowsFull = false
        }

        return allRowsFull
    }

    private fun setLeaders(results: MutableList<Int?>) {
        for (i in results.indices) {
            _leaders.value?.add(Pair(i, results[i]!!))
        }

        _leaders.value?.sortBy { it.second }
        _leaders.value?.reverse()

        for (i in 0 until _leaders.value?.size!!) {
            _leaders.value!![i] = Pair(_leaders.value!![i].first, i + 1)
        }

        _leaders.postValue(_leaders.value)
    }

}