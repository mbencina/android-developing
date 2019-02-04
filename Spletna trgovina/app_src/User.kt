package com.example.djabe.spletnatrgovina

import java.io.Serializable

data class Role (
    val id: Int = 0,
    val name: String = "") : Serializable

data class User (
    val id: Int = 0,
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",
    val status: String? = "",
    val phone: String? = "",
    val email_verified_at: String? = "",
    val created_at: String? = "",
    val updated_at: String? = "",
    val postal_code: Int? = 0,
    val role: Role) : Serializable