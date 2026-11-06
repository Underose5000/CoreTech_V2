package com.example.coretechv2.repository

import android.util.Log
import com.example.coretechv2.dataclasses.APICallTables
import java.time.LocalDate

/**
 * Escapes single quotes so a value can be placed inside a SQL string literal.
 */
fun sqlText(value: String): String = value.replace("'", "''")

/**
 * Issues a quantity against an assembly line by inserting it into
 * ASSEMBLYLINEISSUES.
 *
 * Issues happen as soon as material is added to an assembly (by scanning, by
 * entering a qty, by long pressing a line or by saving an adjustment), so the
 * line's TOTALISSUEDQTY is its actual quantity. Completing the order only
 * creates the receipt.
 *
 * Item codes are issued from the order's default issue warehouse and
 * location, falling back to the item's default receipt warehouse and location
 * when the order has none. Descriptor and other codes are issued without a
 * warehouse. The issue is linked to the assembly line through
 * HEADERSYSUNIQUEID.
 *
 * A negative [qty] reverses an earlier issue, e.g. when an adjustment is
 * reduced or deleted.
 *
 * @param apiCall The API used to run the queries.
 * @param line The assembly line being issued to.
 * @param qty The quantity to issue.
 * @param batchNo The batch number of the material, or null if unknown.
 * @param user The current user, recorded against the issue.
 * @return True if the issue was saved.
 */
suspend fun issueToAssemblyLine(
    apiCall: APICall,
    line: APICallTables.AssemblyLines,
    qty: Double,
    batchNo: String?,
    user: String,
): Boolean {
    var warehouse = "NULL"
    var location = "NULL"
    if (line.CODETYPE == "Item Code") {
        val locationCall: List<APICallTables.IssueLocation>? = apiCall.query(
            "SELECT COALESCE(NULLIF(AH.DEFAULTISSUEWAREHOUSE, ''), IM.DEFAULTRECEIPTWHOUSE) AS WAREHOUSE, " +
                    "COALESCE(NULLIF(AH.DEFAULTISSUELOCATION, ''), IM.DEFAULTRECEIPTLOCATION) AS LOCATION " +
                    "FROM ASSEMBLYHEADER AH LEFT JOIN ITEMMASTER IM ON IM.ITEMCODE = '${sqlText(line.LINECODE)}' " +
                    "WHERE AH.ORDERNUMBER = '${sqlText(line.ORDERNUMBER)}'"
        )
        locationCall?.firstOrNull()?.WAREHOUSE?.takeIf { it.isNotBlank() }?.let { warehouse = "'${sqlText(it)}'" }
        locationCall?.firstOrNull()?.LOCATION?.takeIf { it.isNotBlank() }?.let { location = "'${sqlText(it)}'" }
    }
    val batch = batchNo?.takeIf { it.isNotBlank() }?.let { "'${sqlText(it)}'" } ?: "NULL"
    val issueDate = toDateFormatYYYYMMDD(LocalDate.now())

    val call = "INSERT INTO ASSEMBLYLINEISSUES (" +
            "ORDERNUMBER, COMPONENTTYPE, COMPONENTCODE, COMPONENTDESCRIPTION, COMPONENTUNIT, ISSUEQTY, ISSUEDATE, " +
            "ISSUEWAREHOUSE, ISSUELOCATION, BATCHNO, ISSUESOURCE, HEADERSYSUNIQUEID, SYSUSERCREATED, SYSUSERMODIFIED" +
            ") VALUES (" +
            "'${sqlText(line.ORDERNUMBER)}', '${sqlText(line.CODETYPE)}', '${sqlText(line.LINECODE)}', '${sqlText(line.LINEDESCRIPTION)}', '${sqlText(line.LINEUNIT)}', " +
            "${qty.toBigDecimal().toPlainString()}, '$issueDate', $warehouse, $location, $batch, 'Mobility', " +
            "${line.SYSUNIQUEID.toLong()}, '${sqlText(user)}', '${sqlText(user)}'" +
            ")"

    val ok = apiCall.insertUpdateDelete(call) == "200 OK"
    if (!ok) Log.w("Assembly Issue", "Issue failed: $call")
    return ok
}

/**
 * Loads a single assembly line from the database.
 *
 * @return The line, or null if it could not be found or loaded.
 */
suspend fun loadAssemblyLine(apiCall: APICall, orderNumber: String, lineCode: String, lineNumber: Int): APICallTables.AssemblyLines? {
    val call: List<APICallTables.AssemblyLines>? = apiCall.query(
        "SELECT * FROM AssemblyLines WHERE ORDERNUMBER = '${sqlText(orderNumber)}' " +
                "AND LINECODE = '${sqlText(lineCode)}' AND LINENUMBER = $lineNumber"
    )
    return call?.firstOrNull()
}

/**
 * Returns true if an order with this status can still have material issued
 * to it. Finished and Closed orders cannot.
 */
fun orderAcceptsIssues(orderStatus: String?): Boolean =
    orderStatus !in listOf("Finished", "Closed")
