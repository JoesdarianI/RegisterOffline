package joes.app.registeroffline.data.remote

import joes.app.registeroffline.data.model.LoginRequest
import joes.app.registeroffline.data.model.LoginResponse
import joes.app.registeroffline.data.model.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/v1/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/v1/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @Multipart
    @POST("api/v1/member")
    suspend fun uploadMember(
        @Header("Authorization") token: String,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part ktp_file: MultipartBody.Part?,
        @Part ktp_file_secondary: MultipartBody.Part?
    ): Response<Unit>
}
