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
        val url = "https://$apiUrl:$apiPort/executesql?&format=json&exesql=1&apikey=$apiKey"

        return try {
            val response: HttpResponse = client.post(url){
                contentType(ContentType.Application.Json)
                setBody(sqlsend)
            }
            Log.d("API Call","response = " + response.status.toString() + ", " + response.bodyAsText())
            response.status.toString()


        } catch (e: ClientRequestException) {
            Log.d("API Call","API error: ${e.response.status}, ${e.response.bodyAsText()}")
            null
        } catch (e: Exception) {
            Log.d("API Call","Unexpected error: $e")
            null
        }
    }
}