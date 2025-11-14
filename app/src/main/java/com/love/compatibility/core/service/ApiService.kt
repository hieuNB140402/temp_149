package com.love.compatibility.core.service
import com.love.compatibility.data.model.PartAPI
import retrofit2.Response
import retrofit2.http.GET
interface ApiService {
    @GET("/api/fajsdhf")
    suspend fun getAllData(): Response<Map<String, List<PartAPI>>>
}