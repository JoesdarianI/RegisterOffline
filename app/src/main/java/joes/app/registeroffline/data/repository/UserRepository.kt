package joes.app.registeroffline.data.repository

import joes.app.registeroffline.data.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface UserRepository {
    // Auth operations
    fun getLoggedInUser(): Flow<User?>
    suspend fun saveLoggedInUser(user: User)
    suspend fun clearLoggedInUser()
    
    suspend fun login(request: LoginRequest): Response<LoginResponse>
    suspend fun getProfile(token: String): Response<ProfileResponse>

    // Member operations
    fun getDrafts(): Flow<List<Member>>
    fun getUploadedMembers(): Flow<List<Member>>
    suspend fun saveMember(member: Member)
    suspend fun deleteMember(id: Int)
    suspend fun uploadMember(token: String, partMap: Map<String, RequestBody>, ktpPart: MultipartBody.Part?, ktpSecondaryPart: MultipartBody.Part?): Response<Unit>
}
