package com.dwanz.multiprotocolbenchmark.data.remote.rest

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

data class RestCharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String
)

interface RestApi {
    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): RestCharacterDto
}
