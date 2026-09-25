package com.example.coretechv2.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.NoteTypes


suspend fun testAndAdjustmentLookup(dataStoreManager: DataStoreManager, orderNumber: String): Pair<SnapshotStateList<SnapshotStateList<Any>>, String> {
    val apiCall = APICall(dataStoreManager)
    val testCount = mutableListOf<Int>()
    val testAndAdjustments = mutableStateListOf<SnapshotStateList<Any>>()
    var notes by mutableStateOf("")
        val viscosityTests: List<APICallTables.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_VISCOSITY_TESTS where OrderNumber = '$orderNumber'")
        testCount.addAll(viscosityTests?.map { it.TESTNO } ?: emptyList())

        val gelTimeTests: List<APICallTables.gelTimeTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '$orderNumber'")
        testCount.addAll(gelTimeTests?.map { it.TESTNO } ?: emptyList())

        val elongationTests: List<APICallTables.ElongationalBreakTest>? = apiCall.query("SELECT * FROM OSTDEF_ELONGATIONAL_TEST where OrderNumber = '$orderNumber'")
        testCount.addAll(elongationTests?.map { it.TESTNO } ?: emptyList())

        val flameTests: List<APICallTables.FlammabilityTest>? = apiCall.query("SELECT * FROM OSTDEF_FLAMMABILITY_TEST where OrderNumber = '$orderNumber'")
        testCount.addAll(flameTests?.map { it.TESTNO } ?: emptyList())

        val resistivityTests: List<APICallTables.ResistivityTest>? = apiCall.query("SELECT * FROM OSTDEF_RESISTIVITY_TEST where OrderNumber = '$orderNumber'")
        testCount.addAll(resistivityTests?.map { it.TESTNO } ?: emptyList())

        val peakExothermTests: List<APICallTables.peakExothermTest>? = apiCall.query("SELECT * FROM OSTDEF_PEAKEXOTHERM_TEST where OrderNumber = '$orderNumber'")
        testCount.addAll(peakExothermTests?.map { it.TESTNO } ?: emptyList())

        val adjustmentLines: List<APICallTables.assemblyAdjustment>? = apiCall.query("SELECT * FROM OSTDEF_ADJUSTMENTS where OrderNumber = '$orderNumber'")
        testCount.addAll(adjustmentLines?.map { it.ADJUSTNO } ?: emptyList())

        val notesline: List<APICallTables.notes>? =
            apiCall.query("SELECT * FROM OSTDEF_NOTES where IDNUMBER = '$orderNumber' and TYPE = '${NoteTypes.ASSEMBLY.toStringName()}'")


        val testAndAdjustmentsCount = testCount.toMutableList()
        if (testAndAdjustmentsCount.isEmpty()) {
            testAndAdjustmentsCount.add(0)
        }
        testAndAdjustmentsCount.sortDescending()


        testAndAdjustments.clear()
        for (testValue in 0 until testAndAdjustmentsCount.first()) {
            val testAndAdjustment = mutableStateListOf<Any>()
            val test = mutableStateListOf<Any>()
            val adjust = mutableStateListOf<Any>()
            for (tn in 0 until (viscosityTests?.size ?: 0)) {
                if (viscosityTests?.get(tn)?.TESTNO == testValue + 1) test.add(viscosityTests[tn])
            }
            for (tn in 0 until (gelTimeTests?.size ?: 0)) {
                if (gelTimeTests?.get(tn)?.TESTNO == testValue + 1) test.add(gelTimeTests[tn])
            }
            for (tn in 0 until (elongationTests?.size ?: 0)) {
                if (elongationTests?.get(tn)?.TESTNO == testValue + 1) test.add(elongationTests[tn])
            }
            for (tn in 0 until (flameTests?.size ?: 0)) {
                if (flameTests?.get(tn)?.TESTNO == testValue + 1) test.add(flameTests[tn])
            }
            for (tn in 0 until (resistivityTests?.size ?: 0)) {
                if (resistivityTests?.get(tn)?.TESTNO == testValue + 1) test.add(resistivityTests[tn])
            }
            for (tn in 0 until (peakExothermTests?.size ?: 0)) {
                if (peakExothermTests?.get(tn)?.TESTNO == testValue + 1) test.add(peakExothermTests[tn])
            }
            for (tn in 0 until (adjustmentLines?.size ?: 0)) {
                if (adjustmentLines?.get(tn)?.ADJUSTNO == testValue + 1) adjust.add(adjustmentLines[tn])
            }

            testAndAdjustment.add(test)
            testAndAdjustment.add(adjust)

            testAndAdjustments.add(testAndAdjustment)
        }

        notes = ""
        for (NN in 0 until (notesline?.size ?: 0)) {
            notes += notesline?.get(NN)?.NOTE
            notes += "\n\n"
        }
    return Pair(testAndAdjustments, notes)
}