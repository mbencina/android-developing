package com.example.djabe.spletnatrgovina

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class UserInfoActivity : AppCompatActivity(), Callback<User> {

    private var uporabnik: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = intent.getStringExtra("ep.rest.user")
        uporabnik = user
        UserService.instance.getUser("application/json", user).enqueue(this)
    }

    override fun onResponse(call: Call<User>, response: Response<User>) {
        val userGet = response.body()
        Log.i(UserInfoActivity.TAG, "Got result: $userGet")
        if (response.isSuccessful) {
            // posljemo podatke, da jih lahko prikazemo uporabniku v EditProfileActivity
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("ep.rest.user", uporabnik)
            intent.putExtra("user_name", userGet.name)
            intent.putExtra("user_surname", userGet.surname)
            intent.putExtra("user_email", userGet.email)
            intent.putExtra("user_phone", userGet.phone)
            startActivity(intent)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody().string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, errorMessage)
        }
    }

    override fun onFailure(call: Call<User>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = UserInfoActivity::class.java.canonicalName
    }
}
