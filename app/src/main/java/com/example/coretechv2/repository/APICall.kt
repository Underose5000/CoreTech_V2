package com.example.coretechv2.repository

import android.content.Context
import android.icu.text.DateFormat
import android.util.Log
import androidx.datastore.dataStore
import com.example.coretechv2.MainActivity
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.sql.Blob
import java.sql.DriverManager.println
import java.util.Date


class APICall(private val dataStoreManager: DataStoreManager) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    internal suspend inline fun <reified T> query(sqlsend: String): List<T>? {
        val apiUrl = dataStoreManager.apiUrlFlow.firstOrNull()
        val apiPort = dataStoreManager.apiPortFlow.firstOrNull()
        val apiKey = dataStoreManager.apiKeyFlow.firstOrNull()
        val url = "https://$apiUrl:$apiPort/sqlquery?&format=json&exesql=1&apikey=$apiKey"

        return try {
            val response: HttpResponse = client.post(url){
                contentType(ContentType.Application.Json)
                setBody(sqlsend)
            }
            response.body()


        } catch (e: ClientRequestException) {
            Log.d("API Call","API error: ${e.response.status}, ${e.response.bodyAsText()}")
            null
        } catch (e: Exception) {
            Log.d("API Call","Unexpected error: $e")
            null
        }
    }

    internal suspend inline fun insertUpdateDelete(sqlsend: String): String? {
        val apiUrl = dataStoreManager.apiUrlFlow.firstOrNull()
        val apiPort = dataStoreManager.apiPortFlow.firstOrNull()
        val apiKey = dataStoreManager.apiKeyFlow.firstOrNull()
        val url = "https://$apiUrl:$apiPort/sqlquery?&format=json&exesql=1&apikey=$apiKey"

        return try {
            val response: HttpResponse = client.post(url){
                contentType(ContentType.Application.Json)
                setBody(sqlsend)
            }
            response.status.toString()


        } catch (e: ClientRequestException) {
            Log.d("API Call","API error: ${e.response.status}, ${e.response.bodyAsText()}")
            null
        } catch (e: Exception) {
            Log.d("API Call","Unexpected error: $e")
            null
        }
    }

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
        val ADDITIONALFIELD_1: String,
        val ADDITIONALFIELD_2: String,
        var ADDITIONALFIELD_3: String,
        val ADDITIONALFIELD_4: String,
        val ADDITIONALFIELD_5: String,
        val ADDITIONALFIELD_6: String,
        val ADDITIONALFIELD_7: String,
        var ADDITIONALFIELD_8: String,
        val ADDITIONALFIELD_9: String,
        val ADDITIONALFIELD_10: String,
        val ADDITIONALFIELD_11: String,
        val ADDITIONALFIELD_12: String,
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
        val READING0_3: Double,
        val NOTES: String
    )
    @Serializable
    data class VerifyUserPassword(
        val IS_VALID: Int
    )
    @Serializable
    data class VerifyConnection(
        val IS_CONNECTED: Int
    )


}