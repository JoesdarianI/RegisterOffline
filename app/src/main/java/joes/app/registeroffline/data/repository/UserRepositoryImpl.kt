package joes.app.registeroffline.data.repository

import joes.app.registeroffline.data.local.MemberDao
import joes.app.registeroffline.data.local.UserDao
import joes.app.registeroffline.data.model.LoginRequest
import joes.app.registeroffline.data.model.LoginResponse
import joes.app.registeroffline.data.model.Member
import joes.app.registeroffline.data.model.ProfileResponse
import joes.app.registeroffline.data.model.User
import joes.app.registeroffline.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val memberDao: MemberDao,
    private val apiService: ApiService
) : UserRepository {

    override fun getLoggedInUser(): Flow<User?> = userDao.getLoggedInUser()

    override suspend fun saveLoggedInUser(user: User) {
        userDao.insertUser(user)
    }

    override suspend fun clearLoggedInUser() {
        userDao.clearUser()
    }

    override suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    override suspend fun getProfile(token: String): Response<ProfileResponse> {
        return apiService.getProfile("Bearer $token")
    }

    override fun getDrafts(): Flow<List<Member>> {
        return memberDao.getMembersByStatus("drafted")
    }

    override fun getUploadedMembers(): Flow<List<Member>> {
        return memberDao.getMembersByStatus("uploaded")
    }

    override suspend fun saveMember(member: Member) {
        memberDao.insertMember(member)
    }

    override suspend fun deleteMember(id: Int) {
        memberDao.deleteMember(id)
    }

    override suspend fun uploadMember(
        token: String,
        partMap: Map<String, RequestBody>,
        ktpPart: MultipartBody.Part?,
        ktpSecondaryPart: MultipartBody.Part?
    ): Response<Unit> {
        return apiService.uploadMember("Bearer $token", partMap, ktpPart, ktpSecondaryPart)
    }
}
