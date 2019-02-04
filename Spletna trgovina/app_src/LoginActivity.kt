package com.example.djabe.spletnatrgovina

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), Callback<User> {

    private var uporabnik: String? = ""
    private var userGet: User? = null

    private var dl: DrawerLayout? = null
    private var abdt: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null

    var uname = ""
    var passwd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Prijava")
        setContentView(R.layout.activity_login)

        val user = intent.getStringExtra("ep.rest.user")
        uporabnik = user

        // Spreminjanje visibilityja
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val logout = navView.menu.findItem(R.id.logout)

        // ce je uporabnik prijavljen
        if (user != null && user.length > 2 ) {
            val login = navView.menu.findItem(R.id.login)
            val myprofile = navView.menu.findItem(R.id.myprofile)

            logout.isVisible = true
            myprofile.isVisible = true
            login.isVisible = false
        }
        // ce se je uporabnik odjavil
        if (user == "" && logout.isVisible == true) {
            val login = navView.menu.findItem(R.id.login)
            val myprofile = navView.menu.findItem(R.id.myprofile)

            logout.isVisible = false
            myprofile.isVisible = false
            login.isVisible = true
        }

        btnSave.setOnClickListener {
            uname = username.text.toString().trim()
            passwd = password.text.toString().trim()
            val concat = uname.plus(":").plus(passwd)
            val bytes = concat.toByteArray()
            val login64 = Base64.encodeToString(bytes, 0)
            val login = "Basic ".plus(login64).trim()
            // zdej bom zravn skos glavo posilju
            uporabnik = login
            UserService.instance.getUser("application/json", login).enqueue(this)
        }

        //NAVBAR
        dl = findViewById(R.id.dl) as DrawerLayout
        abdt = ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close)

        dl!!.addDrawerListener(abdt!!)
        abdt!!.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nv = findViewById(R.id.nav_view)
        nv!!.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ep.rest.user", uporabnik)
                    startActivity(intent)
                    true
                }
                R.id.login -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("ep.rest.user", uporabnik)
                    startActivity(intent)
                    true
                }
                R.id.myprofile -> {
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("ep.rest.user", uporabnik)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    //resetiramo userja
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ep.rest.user", "")
                    startActivity(intent)
                    true
                }
                else -> true
            }
        }
    }

    override fun onResponse(call: Call<User>, response: Response<User>) {
        userGet = response.body()
        Log.i(LoginActivity.TAG, "Got result: $userGet")
        if (response.isSuccessful) {
            Toast.makeText(this, "Prijava uspešna", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ep.rest.user", uporabnik)
            startActivity(intent)
        } else {
            // uporabnik ne obstaja zato resetiramo
            val errorMessage = try {
                "An error occurred: ${response.errorBody().string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }
            Toast.makeText(this, "Uporabniško ime ali geslo je napačno", Toast.LENGTH_SHORT).show()
            uporabnik = ""
            Log.e(TAG, errorMessage)
        }
    }

    override fun onFailure(call: Call<User>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = LoginActivity::class.java.canonicalName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (abdt!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }
}