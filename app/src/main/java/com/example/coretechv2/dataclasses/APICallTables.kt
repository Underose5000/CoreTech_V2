package com.example.coretechv2.dataclasses

import kotlinx.serialization.Serializable

class APICallTables {

    @Serializable
    data class ItemMaster(
        val ITEMCODE: String,
        val ITEMDESCRIPTION: String,
        val ITEMUNIT: String,
        val AVAILABLEQTY: Float
    )


    @Serializable
    data class AssemblyHeader(
        val ORDERNUMBER: String,
        val ORDERSTATUS: String,
        val ORDERDATE: String,
        val ITEMCODE: String,
        val ITEMDESCRIPTION: String,
        val ITEMUNIT: String,
        val REQUIREDDATE: String,
        val ORDERQTY: Double,
        val COMPLETEQTY : Double,
        val REMAININGQTY: Double,
        val ASSEMBLYVERSION: String,
        var ADDITIONALFIELD_1: String,
        var ADDITIONALFIELD_2: String,
        var ADDITIONALFIELD_3: String,
        var ADDITIONALFIELD_4: String,
        var ADDITIONALFIELD_5: String,
        var ADDITIONALFIELD_6: String,
        var ADDITIONALFIELD_7: String,
        var ADDITIONALFIELD_8: String,
        var ADDITIONALFIELD_9: String,
        var ADDITIONALFIELD_10: String,
        var ADDITIONALFIELD_11: String,
        var ADDITIONALFIELD_12: String,
    )


    @Serializable
    data class AssemblyLines(
        val ORDERNUMBER: String,
        val STEPNAME: String,
        val STEPSEQUENCE: Int,
        val LINESTATUS: String,
        val LINENUMBER: Int,
        val CODETYPE: String,
        val LINECODE: String,
        val LINEDESCRIPTION: String,
        val LINEUNIT : String,
        val ORDERQTY: Double,
        val TOTALISSUEDQTY: Double,
        val REMAININGQTY: Double,
        val POSITIONREFERENCE: String,
        val LINENOTES: String,
        val HEADERSYSUNIQUEID: Double,
        val ADDITIONALFIELD_1: String,
        val ADDITIONALFIELD_2: String,
        val ADDITIONALFIELD_3: String,
        val ADDITIONALFIELD_4: String,
        val ADDITIONALFIELD_6: String,
    )


    @Serializable
    data class viscosityTest(
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val ORDERNUMBER: String,
        val TESTNO: Int,
        val ITEMDESCRIPTION: String,
        val SPINDLE: String,
        val INDEXREADING: Double,
        val READING60: Double,
        val READING30: Double,
        val READING12: Double,
        val READING6: Double,
        val READING3: Double,
        val READING1_5: Double,
        val READING0_6: Double,
        val READING0_3: Double
    )


    @Serializable
    data class gelTimeTest(
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val ORDERNUMBER: String,
        val TESTNO: Int,
        val ITEMDESCRIPTION: String,
        val GELTIME: String,
        val GELCAT: String,
        val GELCATPERCENT: String,
    )

    @Serializable
    data class VerifyUserPassword(
        val IS_VALID: Int
    )


    @Serializable
    data class VerifyConnection(
        val IS_CONNECTED: Int
    )


    @Serializable
    data class Count(
        val COUNT: Int
    )
}