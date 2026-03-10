package com.example.coretechv2.repository

import android.content.Context
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.sql.DriverManager.println


class APICall(private val dataStoreManager: DataStoreManager) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    internal suspend inline fun <reified T> query(sqlsend: String): List<T>? {

        val apiUrl = dataStoreManager.apiUrlFlow.firstOrNull()
        val apiPort = dataStoreManager.apiPortFlow.firstOrNull()
        val apiKey = dataStoreManager.apiKeyFlow.firstOrNull()
        Log.d("API Call", "url = $apiUrl \nport = $apiPort \nkey = $apiKey ")
        val url = "https://$apiUrl:$apiPort/sqlquery?&format=json&exesql=1&apikey=$apiKey"
        Log.d("API Call","final url = $url")

            return try {
            val response: HttpResponse = client.post(url){
                contentType(ContentType.Application.Json)
                setBody(sqlsend)
            }
                Log.d("API Call","API returned: ${response.status}, ${response.bodyAsText()}")
            response.body()


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
    data class VerifyUserPassword(
        val IS_VALID: Int
    )
    @Serializable
    data class VerifyConnection(
        val IS_CONNECTED: Int
    )


}