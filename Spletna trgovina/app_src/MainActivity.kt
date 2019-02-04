package com.example.djabe.spletnatrgovina

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.djabe.spletnatrgovina.R.id.myprofile
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity(), Callback<List<Product>> {

    private var adapter: ProductAdapter? = null
    private var uporabnik: String? = ""

    private var dl: DrawerLayout? = null
    private var abdt: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null
    private var nav: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ugotovimo kaksen menu moramo displayati
        val user = intent?.getStringExtra("ep.rest.user")
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

        // ITEMS
        adapter = ProductAdapter(this)
        items.adapter = adapter
        items.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val product = adapter?.getItem(i)
            if (product != null) {
                val intent = Intent(this, ProductActivity::class.java)
                intent.putExtra("ep.rest.id", product.id)
                intent.putExtra("ep.rest.user", uporabnik)
                startActivity(intent)
            }
        }

        container.setOnRefreshListener { ProductService.instance.getAll().enqueue(this) }

        ProductService.instance.getAll().enqueue(this)

        nav = findViewById(R.id.nav_view)
        nav?.menu?.setGroupVisible(myprofile, true)

        // NAVBAR
        dl = findViewById(R.id.dl) as DrawerLayout
        abdt = ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close)

        dl!!.addDrawerListener(abdt!!)
        abdt!!.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nv = findViewById(R.id.nav_view)
        val logo = nv?.findViewById<View>(R.id.myprofile)

        logo?.visibility = View.VISIBLE
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

    override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
        val hits = response.body()

        if (response.isSuccessful) {
            Log.i(TAG, "Hits: " + hits.size)
            adapter?.clear()
            adapter?.addAll(hits)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody().string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, errorMessage)
        }
        container.isRefreshing = false
    }

    override fun onFailure(call: Call<List<Product>>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
        container.isRefreshing = false
    }

    companion object {
        private val TAG = MainActivity::class.java.canonicalName
    }
}
