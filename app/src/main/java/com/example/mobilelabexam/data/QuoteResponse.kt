package com.example.mobilelabexam.data

import com.google.gson.annotations.SerializedName

data class QuoteResponse(
    @SerializedName("content") val content: String,
    @SerializedName("author") val author: String
)
