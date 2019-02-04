package com.example.djabe.spletnatrgovina

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import android.text.SpannableStringBuilder

class EditProfileActivity : AppCompatActivity(), Callback<User> {

    private var dl: DrawerLayout? = null
    private var abdt: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null

    private var uporabnik: String? = ""
    private var spremenjeniPodatki: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Moj Profil")
        setContentView(R.layout.activity_edit_profile)

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

        val name_ = intent.getStringExtra("user_name")
        val surname_ = intent.getStringExtra("user_surname")
        val email_ = intent.getStringExtra("user_email")
        val phone_ = intent.getStringExtra("user_phone")

        name.text = SpannableStringBuilder(name_)
        surname.text = SpannableStringBuilder(surname_)
        email.text = SpannableStringBuilder(email_)
        telephone.text = SpannableStringBuilder(phone_)

        btnSave.setOnClickListener{
            // osnovno preverjanje polj
            if (name.text != null && surname.text != null && email.text != null && telephone.text != null) {
                if (name.text.isNotEmpty() && surname.text.isNotEmpty() && email.text.isNotEmpty() && telephone.text.isNotEmpty()) {
                    if (password.text.isEmpty() && password_confirm.text.isEmpty()){
                        if (email_ != email.text.toString()){
                            // uporabnik se bo moral ponovno vpisati
                            spremenjeniPodatki = true
                        }
                        UserService.instance.updateUser(
                            "application/json",
                            user, name.text.toString(),
                            surname.text.toString(),
                            email.text.toString(),
                            telephone.text.toString()
                        ).enqueue(this)
                    } else {
                        if (password_confirm.text.toString() == password.text.toString()) {
                            // uporabnik se bo moral ponovno vpisati
                            spremenjeniPodatki = true
                            UserService.instance.updateUser(
                                "application/json",
                                user,
                                name.text.toString(),
                                surname.text.toString(),
                                email.text.toString(),
                                telephone.text.toString(),
                                password.text.toString()
                            ).enqueue(this)
                        } else {
                            Toast.makeText(this, "Gesli se ne ujemata", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Vsa vnosna polja morajo biti izpolnjena", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vnesite veljavne podatke", Toast.LENGTH_SHORT).show()
            }

        }
        dl = findViewById(R.id.dl) as DrawerLayout
        abdt = ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close)

        dl!!.addDrawerListener(abdt!!)
        abdt!!.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nv = findViewById(R.id.nav_view) as NavigationView
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (abdt!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onResponse(call: Call<User>, response: Response<User>) {
        val userPut = response.body()
        Log.i(EditProfileActivity.TAG, "Got result: $userPut")
        if (response.isSuccessful) {
            Toast.makeText(this, "Podatki so bili uspešno posodobljeni", Toast.LENGTH_SHORT).show()
            if (!spremenjeniPodatki){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ep.rest.user", uporabnik)
                startActivity(intent)
            } else {
                // uporavnik se bo moral ponovno prijaviti
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("ep.rest.user", "")
                startActivity(intent)
            }
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
        private val TAG = EditProfileActivity::class.java.canonicalName
    }
}