package com.example.djabe.spletnatrgovina

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.*

class ProductActivity : AppCompatActivity(), Callback<Product> {

    private var product: Product? = null

    private var uporabnik: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        // preberemo intent
        val user = intent.getStringExtra("ep.rest.user")
        uporabnik = user

        val idArtikla = intent.getIntExtra("ep.rest.id", 0)

        if (idArtikla > 0) {
            ProductService.instance.get(idArtikla).enqueue(this)
        }
    }

    override fun onResponse(call: Call<Product>, response: Response<Product>) {
        product = response.body()
        Log.i(TAG, "Got result: $product")

        if (response.isSuccessful) {
            val stOcen = product?.rating?.num_ratings
            var ocena = "?"
            var numOcena = 0
            if (stOcen != null && stOcen > 0) {
                val sumOcen = product?.rating?.rating
                if (sumOcen != null)
                    numOcena = sumOcen / stOcen
                    ocena = numOcena.toString()
            }
            //TODO URL
            if (product?.images?.isEmpty() == false) {
                val urlSlike = "http://193.2.179.144:8080".plus(product?.images?.first()?.path)
                Picasso.get().load(urlSlike).into(slikaArtikla)
            }
            imeArtikla.text = product?.name
            setTitle(product?.name)
            ocenaArtikla.text = ocena.plus("/5")
            if (numOcena <= 2) ocenaArtikla.setTextColor(ResourcesCompat.getColor(resources, R.color.product2, null))
            if (numOcena <= 1) ocenaArtikla.setTextColor(ResourcesCompat.getColor(resources, R.color.product1, null))
            if (numOcena > 4) ocenaArtikla.setTextColor(ResourcesCompat.getColor(resources, R.color.product5, null))
            cenaArtikla.text = String.format(Locale.ENGLISH, "%.2f $", product?.price)
            opisArtikla.text = product?.description
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody().string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Log.e(TAG, errorMessage)
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call<Product>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = ProductActivity::class.java.canonicalName
    }
}
