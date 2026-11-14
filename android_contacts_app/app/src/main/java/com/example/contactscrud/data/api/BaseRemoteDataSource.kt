package com.example.contactscrud.data.api

import com.example.contactscrud.data.Result
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class BaseRemoteDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Result.success(body)
                // Successful but empty body (e.g. 204 No Content) — treat as success(null-safe)
                @Suppress("UNCHECKED_CAST")
                return Result.success(Unit as T)
            }
            Result.error("Request failed: ${response.code()} ${response.message()}")
        } catch (e: ConnectException) {
            Result.error(SERVER_DOWN_MESSAGE)
        } catch (e: SocketTimeoutException) {
            Result.error(SERVER_DOWN_MESSAGE)
        } catch (e: UnknownHostException) {
            Result.error(SERVER_DOWN_MESSAGE)
        } catch (e: IOException) {
            // Any other connectivity problem (no network, connection reset, etc.)
            Result.error(SERVER_DOWN_MESSAGE)
        } catch (e: Exception) {
            Result.error("Something went wrong: ${e.message ?: e.toString()}")
        }
    }

    companion object {
        private const val SERVER_DOWN_MESSAGE =
            "Cannot reach the server.\nIs the backend running on http://localhost:8080 ?"
    }
}
