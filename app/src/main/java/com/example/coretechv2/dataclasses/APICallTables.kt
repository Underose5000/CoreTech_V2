package com.example.coretechv2.dataclasses

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects (DTOs) used for API communication between the app and backend.
 *
 * This file contains all database-mapped structures used for:
 * - Assembly management (headers, lines, adjustments)
 * - Testing modules (viscosity, gel time)
 * - Item master data
 * - Utility responses (validation, counts, connection checks)
 *
 * All models are marked with [kotlinx.serialization.Serializable]
 * to allow JSON parsing between API requests and responses.
 *
 * These classes directly map to database tables or query results
 * and are used primarily by the repository layer and ViewModels.
 */
class APICallTables {

    @Serializable
    data class ItemMaster(
        val ITEMCODE: String,
        val ITEMDESCRIPTION: String,
        val ITEMUNIT: String,
        val AVAILABLEQTY: Float,
        val DEFAULTRECEIPTWHOUSE: String,
        val DEFAULTRECEIPTLOCATION: String,
    )


    @Serializable
    data class ItemDescriptor(
        val CODE: String,
        val DESCRIPTION: String,
        val UNIT: String,
        val STATUS: String,
        val BARCODE: String,
        val CATEGORY: String,
        val ONHANDQTY: Double,
        val SUPPLYQTY: Double,
        val DEMANDQTY: Double,
        val AVAILABLEQTY: Double,
        val FREEQTY: Double,
        val TYPE: String,
        val SYSUNIQUEID: Int
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
        val ORDERNOTES: String,
        val ORDERQTY: Double,
        val COMPLETEQTY: Double,
        val REMAININGQTY: Double,
        val ASSEMBLYVERSION: String,
        val PLANNEDTOTALCOSTS: Double,
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
        var ADDITIONALFIELD_13: String,
        val SYSUNIQUEID: Double,
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
        val LINEUNIT: String,
        var ORDERQTY: Double,
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
        val SYSUNIQUEID: Double,
    )


    @Serializable
    data class assemblyBOMSteps(

        val ITEMCODE: String?,
        val STEPNAME: String,

        )

    @Serializable
    data class assemblyBOMMaster(
        val ASSEMBLYCODE: String,
        val ASSEMBLYVERSION: String,
        val VERSIONDESCRIPTION: String,
        val VERSIONSTATUS: String,
        val ITEMCODE: String,
        val ITEMDESCRIPTION: String,
        val ITEMUNIT: String,
        val ITEMSTATUS: String,
        val ASSEMBLYINSTRUCTIONS: String,
        val ASSEMBLYLEADTIME: Int,
        val ASSEMBLYDURATION: Double,
        val ASSEMBLYDURATIONSCALE: String,
        val BATCHQTY: Double,

        )

    @Serializable
    data class assemblyBOMLines(
        val ITEMCODE: String,
        val STEPSEQUENCE: Int,
        val STEPNAME: String,
        val LINENUMBER: Int,
        val CODETYPE: String,
        val LINECODE: String,
        val LINEDESCRIPTION: String,
        val LINEUNIT: String,
        val PERQTY: Double,
        val PERBATCHQTY: Double,
        val LINESCRAPPERCENT: Double,
        val RUNORSETUP: String,
        val POSITIONREFERENCE: String,
        val LINEINSTRUCTIONS: String,
        val HEADERSYSUNIQUEID: Double,
        val SYSUNIQUEID: Double,

        )

    @Serializable
    data class notes(
        val SYSUNIQUEID: Double,
        val TYPE: String,
        val IDNUMBER: String,
        val NOTE: String,
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
    data class peakExothermTest(
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val ORDERNUMBER: String,
        val TESTNO: Int,
        val ITEMDESCRIPTION: String,
        val GELTIME: String,
        val GELCAT: String,
        val GELCATPERCENT: String,
        val PEAKTEMPERATURE: Double
    )

    @Serializable
    data class FlammabilityTest(
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val ORDERNUMBER: String,
        val TESTNO: Int,
        val ITEMDESCRIPTION: String,
        val FLAMETIME: String,
        val DAYSSET: Int,
        val BURNLENGTH: Double,
    )

    @Serializable
    data class ElongationalBreakTest(
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val ORDERNUMBER: String,
        val TESTNO: Int,
        val ITEMDESCRIPTION: String,
        val DAYSSET: Int,
        val ELONGATIONPERCENT: Double
    )

    @Serializable
    data class ResistivityTest(
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val ORDERNUMBER: String,
        val TESTNO: Int,
        val ITEMDESCRIPTION: String,
        val DAYSSET: Int,
        val RESISTIVITYOHM: Double
    )

    @Serializable
    data class assemblyAdjustment(
        val SYSUNIQUEID: Double,
        val ORDERNUMBER: String,
        val LINENUMBER: Int,
        val CODETYPE: String,
        val LINECODE: String,
        val ADJUSTQTY: Double,
        val ADJUSTNO: Int,
        val LINEDESCRIPTION: String,
        val LINEUNIT: String,
    )


    @Serializable
    data class assemblyLabelLayout(
        val LABELID: LabelStyles,
        val PAGEWIDTH: Double,
        val PAGEHEIGHT: Double,
        val PAGEPAD: Double,
        val MIDWIDTH: Double,
        val STRIPSPOS: Double,
        val LOGOPOS: Double,
        val LOGOSIZE: Double,
        val LOGOOFFSET: Double,
        val LOGOSMALLSIZE: Double,
        val ADDPIGMENTSIZE: Double,
        val ROADMARINESIZE: Double,
        val QRCODESIZE: Double,
        val PICTOSIZE: Double,
        val COLORPOS: Double,
        val TOPNAMEPOS: Double,
        val BOTTOMNAMEPOS: Double,
        val MIDNAMEPOS: Double,
        val DGPOS: Double,
        val VARIANTPOS: Double,
        val VARIANTPADHOZ: Double,
        val VARIANTPADVER: Double,
        val SIZEPOS: Double,
        val WARNINGPOS: Double,
        val PICTOGAP: Double,
        val TOPNAMEFS: Double,
        val BOTTOMNAMEFS: Double,
        val DGFS: Double,
        val VARIANTFS: Double,
        val SIZEFS: Double,
        val HEADINGFS: Double,
        val SUBHEADFS: Double,
        val BODYFS: Double,
        val SPACEING: Double,
    )

    @Serializable
    data class assemblyLabelItemInfo(
        val HEADERSYSUNIQUEID: Double,
        val SYSUNIQUEID: Double,
        val ITEMCODE: String,
        val TOPNAME: String,
        val MIDDLENAME: String,
        val BOTTOMNAME: String,
        val SIZE: String,
        val QRCODE: String,
        val VARIANT: String,
        val BESTBEFORE: Int,
        val LABELSTYLE: LabelStyles,
        var BOXQTY: Int,
        val ITEMBARCODE: String,
    )

    @Serializable
    data class assemblyLabelClassInfo(
        val HEADERSYSUNIQUEID: Double,
        val SYSUNIQUEID: Double,
        val GROUPTYPE: String,
        val WARNINGSIGN: String,
        val DIRECTIONS: String,
        val HELPTIPTIN: String,
        val HELPTIPPAIL: String,
        val PRECAUTIONS: String,
        val INGESTION: String,
        val SKINCONTACT: String,
        val EYECONTACT: String,
        val SAFESTORAGE: String,
        val PICTOGRAM1: String,
        val PICTOGRAM2: String,
        val COLOUR: String,
    )


    @Serializable
    data class itemDGInfo(
        val UNNUMBER: String,
        val PACKINGGROUP: String,
        val DGCLASS: String,
        val DGQUANTITY: Double,
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
    data class StringData(
        val STRING: String
    )

    @Serializable
    data class Count(
        val COUNT: Int
    )
}