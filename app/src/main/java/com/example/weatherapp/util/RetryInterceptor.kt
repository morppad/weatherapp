package com.example.weatherapp.util

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        lateinit var response: Response

        do {
            try {
                response = chain.proceed(chain.request())
            } catch (e: IOException) {
                attempt++
                if (attempt >= maxRetries) throw e
                continue
            }
        } while (!response.isSuccessful && attempt < maxRetries)

        return response
    }
}
