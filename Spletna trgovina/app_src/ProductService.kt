package com.example.djabe.spletnatrgovina

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// kot staticni razred v javi

object ProductService {

    interface RestApi {
        //TODO URL
        companion object {
            const val URL = "http://193.2.179.144:8080/"
        }

        @GET("api/products")
        fun getAll(): Call<List<Product>>

        @GET("api/product/{id}")
        fun get(@Path("id") id: Int): Call<Product>

    }

    val instance: RestApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(RestApi.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RestApi::class.java)
    }
}