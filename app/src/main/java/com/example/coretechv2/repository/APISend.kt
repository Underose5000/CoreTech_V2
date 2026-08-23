package com.example.coretechv2.repository

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json


val config = "0"
/**
 * Repository class responsible for communicating with the backend SQL API.
 *
 * This class provides helper functions for:
 * - Executing SQL SELECT queries
 * - Executing INSERT, UPDATE, and DELETE operations
 * - Handling API authentication and endpoint configuration
 * - Parsing JSON responses using Kotlin Serialization
 *
 * API configuration values are retrieved from [DataStoreManager]:
 * - API URL
 * - API port
 * - API key
 *
 * Networking is implemented using Ktor HTTP client with CIO engine.
 *
 * @property dataStoreManager Provides stored API configuration values.
 */
class APICall(private val dataStoreManager: DataStoreManager) {

    /**
     * Shared HTTP client used for all API requests.
     *
     * Configured with:
     * - CIO engine
     * - Kotlinx serialization JSON support
     * - Unknown JSON key tolerance
     */
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    /**
     * Executes a SQL SELECT query against the backend API.
     *
     * The query is sent to the `/sqlquery` endpoint and the response
     * is automatically deserialized into a list of type [T].
     *
     * Example usage:
     * ```
     * val result: List<APICallTables.ItemMaster>? =
     *     apiCall.query("SELECT * FROM ITEMMASTER")
     * ```
     *
     * @param sqlsend SQL query string to execute.
     * @return List of deserialized objects if successful, otherwise null.
     *
     * @throws ClientRequestException Logged when API returns client error response.
     * @throws Exception Logged for unexpected failures.
     */
    internal suspend inline fun <reified T> query(sqlsend: String): List<T>? {
        val apiUrl = dataStoreManager.apiUrlFlow.firstOrNull()
        val apiPort = dataStoreManager.apiPortFlow.firstOrNull()
        val apiKey = dataStoreManager.apiKeyFlow.firstOrNull()
        val url = "https://$apiUrl:$apiPort/sqlquery?&format=json&exesql=1&configuration=$config&apikey=$apiKey"

        return try {
            val response: HttpResponse = client.post(url){
                contentType(ContentType.Application.Json)
                setBody(sqlsend)
            }
            response.body()


        } catch (e: ClientRequestException) {
            Log.d("APICall", "ClientRequestException: ${e.message}", e)
            null
        } catch (e: io.ktor.serialization.JsonConvertException){
            Log.w("APICall", "JsonConvertException:\n Call: $sqlsend\n Error: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("APICall", "Exception while querying API: ${e.message}", e)
            null
        }
    }

    /**
     * Executes a SQL INSERT, UPDATE, or DELETE statement against the backend API.
     *
     * The SQL command is sent to the `/executesql` endpoint.
     *
     * Commonly used for:
     * - Creating records
     * - Updating records
     * - Removing records
     *
     * @param sqlsend SQL command string to execute.
     * @return HTTP status string if successful, otherwise null.
     *
     * Example return:
     * - `"200 OK"`
     *
     * @throws ClientRequestException Logged when API returns client error response.
     * @throws Exception Logged for unexpected failures.
     */
    internal suspend inline fun insertUpdateDelete(sqlsend: String): String? {
        val apiUrl = dataStoreManager.apiUrlFlow.firstOrNull()
        val apiPort = dataStoreManager.apiPortFlow.firstOrNull()
        val apiKey = dataStoreManager.apiKeyFlow.firstOrNull()
        val url = "https://$apiUrl:$apiPort/executesql?&format=json&exesql=1&configuration=$config&apikey=$apiKey"

        return try {
            val response: HttpResponse = client.post(url){
                contentType(ContentType.Application.Json)
                setBody(sqlsend)
            }
            
            response.status.toString()


        } catch (e: ClientRequestException) {
            Log.d("APICall", "ClientRequestException: ${e.message}", e)
            null
        } catch (e: io.ktor.serialization.JsonConvertException){
            Log.w("APICall", "JsonConvertException:\n Call: $sqlsend\n Error: ${e.message}")
                null
        } catch (e: Exception) {
            Log.e("APICall", "Exception while querying API: ${e.message}", e)
            null
        }
    }
}