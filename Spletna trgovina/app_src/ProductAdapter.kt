package com.example.djabe.spletnatrgovina

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import com.squareup.picasso.Picasso

class ProductAdapter(context: Context) : ArrayAdapter<Product>(context, 0, ArrayList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        val view = if (convertView == null)
            LayoutInflater.from(context).inflate(R.layout.productlist_element, parent, false)
        else
            convertView

        val image = view.findViewById<ImageView>(R.id.productImage)
        val name = view.findViewById<TextView>(R.id.productName)
        val price = view.findViewById<TextView>(R.id.productPrice)

        val product = getItem(position)
        //TODO URL
        if (product?.images?.isEmpty() == false) {
            val urlSlike = "http://193.2.179.144:8080".plus(product.images.first().path)
            Picasso.get().load(urlSlike).into(image)
        }
        name.text = product?.name
        price.text = String.format(Locale.ENGLISH, "%.2f $", product?.price)

        return view
    }
}