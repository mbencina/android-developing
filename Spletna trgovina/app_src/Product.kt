package com.example.djabe.spletnatrgovina

import java.io.Serializable

data class Rating (
    val num_ratings: Int = 0,
    val rating: Int = 0) : Serializable

data class Image (
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val path: String = "") : Serializable

data class Product (
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val images: Array<Image> = arrayOf(Image()), //to je object array
    val rating: Rating) : Serializable


