package com.example.coretechv2.dataclasses

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel

enum class TestTypes {
    VISCOSITY, GEL_TIME, FLAME;


    fun toDatabaseHeadingName(): String{
        return when (this) {
            VISCOSITY -> "OSTDEF_VISCOSITY_TESTS"
            GEL_TIME -> "Render"
            FLAME -> "Cladding"
        }
    }
    fun toDatabaseFieldName(): String{
        return when (this) {
            VISCOSITY -> "TESTNO, SPINDLE, INDEXREADING, READING60, READING30, READING12, READING6, READING3, READING1_5, READING0_6, READING0_3"
            GEL_TIME -> "Render"
            FLAME -> "Cladding"
        }
    }
}
