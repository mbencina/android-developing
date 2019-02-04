package com.example.djabe.spletnatrgovina

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object UserService {

    interface RestApi {
        //TODO URL
        companion object {
            const val URL = "http://193.2.179.144:8080/api/"
        }

        @GET("user")
        fun getUser(@Header("Accept") accept: String, @Header("Authorization") login: String) : Call<User>

        //TODO zakaj ta dva requesta ne delata?
        @FormUrlEncoded
        @PUT("user")
        fun updateUser(@Header("Accept") accept: String,
                       @Header("Authorization") login: String,
                       @Field("name") name: String,
                       @Field("surname") surname: String,
                       @Field("email") email: String,
                       @Field("phone") phone: String,
                       @Field("password") password: String): Call<User>

        @FormUrlEncoded
        @PUT("user")
        fun updateUser(@Header("Accept") accept: String,
                       @Header("Authorization") login: String,
                       @Field("name") name: String,
                       @Field("surname") surname: String,
                       @Field("email") email: String,
                       @Field("phone") phone: String): Call<User>
    }

    val instance: RestApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(RestApi.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(RestApi::class.java)
    }
}