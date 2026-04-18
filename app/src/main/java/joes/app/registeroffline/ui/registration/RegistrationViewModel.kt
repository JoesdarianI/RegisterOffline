package joes.app.registeroffline.ui.registration

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import joes.app.registeroffline.data.model.Member
import joes.app.registeroffline.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class RegistrationViewModel(private val repository: UserRepository) : ViewModel() {
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _nik = MutableStateFlow("")
    val nik: StateFlow<String> = _nik.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _birthPlace = MutableStateFlow("")
    val birthPlace: StateFlow<String> = _birthPlace.asStateFlow()

    private val _birthDate = MutableStateFlow("")
    val birthDate: StateFlow<String> = _birthDate.asStateFlow()

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _maritalStatus = MutableStateFlow("")
    val maritalStatus: StateFlow<String> = _maritalStatus.asStateFlow()

    private val _job = MutableStateFlow("")
    val job: StateFlow<String> = _job.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _province = MutableStateFlow("")
    val province: StateFlow<String> = _province.asStateFlow()

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _district = MutableStateFlow("")
    val district: StateFlow<String> = _district.asStateFlow()

    private val _subDistrict = MutableStateFlow("")
    val subDistrict: StateFlow<String> = _subDistrict.asStateFlow()

    private val _postalCode = MutableStateFlow("")
    val postalCode: StateFlow<String> = _postalCode.asStateFlow()

    private val _isDomisiliSame = MutableStateFlow(true)
    val isDomisiliSame: StateFlow<Boolean> = _isDomisiliSame.asStateFlow()

    private val _ktpPath = MutableStateFlow<String?>(null)
    val ktpPath: StateFlow<String?> = _ktpPath.asStateFlow()

    private val _ktpSecondaryPath = MutableStateFlow<String?>(null)
    val ktpSecondaryPath: StateFlow<String?> = _ktpSecondaryPath.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus.asStateFlow()

    fun getDrafts(): Flow<List<Member>> = repository.getDrafts()
    
    fun getUploadedMembers(): Flow<List<Member>> = repository.getUploadedMembers()

    fun onPhoneChange(value: String) = _phone.update { value }
    fun onNikChange(value: String) = _nik.update { value }
    fun onFullNameChange(value: String) = _fullName.update { value }
    fun onBirthPlaceChange(value: String) = _birthPlace.update { value }
    fun onBirthDateChange(value: String) = _birthDate.update { value }
    fun onGenderChange(value: String) = _gender.update { value }
    fun onMaritalStatusChange(value: String) = _maritalStatus.update { value }
    fun onJobChange(value: String) = _job.update { value }
    fun onAddressChange(value: String) = _address.update { value }
    fun onProvinceChange(value: String) = _province.update { value }
    fun onCityChange(value: String) = _city.update { value }
    fun onDistrictChange(value: String) = _district.update { value }
    fun onSubDistrictChange(value: String) = _subDistrict.update { value }
    fun onPostalCodeChange(value: String) = _postalCode.update { value }
    fun onDomisiliSameChange(value: Boolean) = _isDomisiliSame.update { value }

    fun createImageUri(context: Context): Uri {
        val file = File(context.filesDir, "camera_temp_${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun handleKtpImage(path: String) {
        _ktpPath.update { path }
    }

    fun handleKtpSecondaryImage(path: String) {
        _ktpSecondaryPath.update { path }
    }

    fun handleKtpImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            val file = copyAndCompressUriToFile(context, uri)
            _ktpPath.update { file?.absolutePath }
        }
    }

    fun handleKtpSecondaryImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            val file = copyAndCompressUriToFile(context, uri)
            _ktpSecondaryPath.update { file?.absolutePath }
        }
    }

    private suspend fun copyAndCompressUriToFile(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap != null) {
                val file = File(context.filesDir, "ktp_${UUID.randomUUID()}.jpg")
                val outputStream = FileOutputStream(file)
                // Compress to 70% quality to reduce size significantly while keeping it readable
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                outputStream.close()
                file
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun onSaveDraft() {
        viewModelScope.launch {
            val member = Member(
                name = _fullName.value,
                nik = _nik.value,
                phone = _phone.value,
                birthPlace = _birthPlace.value,
                birthDate = _birthDate.value,
                status = _maritalStatus.value,
                occupation = _job.value,
                address = _address.value,
                provinsi = _province.value,
                kotaKabupaten = _city.value,
                kecamatan = _district.value,
                kelurahan = _subDistrict.value,
                kodePos = _postalCode.value,
                alamatDomisili = if (_isDomisiliSame.value) _address.value else "",
                provinsiDomisili = if (_isDomisiliSame.value) _province.value else "",
                kotaKabupatenDomisili = if (_isDomisiliSame.value) _city.value else "",
                kecamatanDomisili = if (_isDomisiliSame.value) _district.value else "",
                kelurahanDomisili = if (_isDomisiliSame.value) _subDistrict.value else "",
                kodePosDomisili = if (_isDomisiliSame.value) _postalCode.value else "",
                ktpPath = _ktpPath.value,
                ktpSecondaryPath = _ktpSecondaryPath.value,
                registrationStatus = "drafted"
            )
            repository.saveMember(member)
            _saveSuccess.update { true }
        }
    }

    fun uploadMember(token: String, member: Member) {
        viewModelScope.launch {
            _uploadStatus.update { "Uploading ${member.name}..." }
            try {
                val partMap = mutableMapOf<String, RequestBody>(
                    "name" to member.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "nik" to member.nik.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "phone" to member.phone.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "birth_place" to member.birthPlace.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "birth_date" to member.birthDate.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "status" to member.status.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "occupation" to member.occupation.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "address" to member.address.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "provinsi" to member.provinsi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kota_kabupaten" to member.kotaKabupaten.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kecamatan" to member.kecamatan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kelurahan" to member.kelurahan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kode_pos" to member.kodePos.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "alamat_domisili" to member.alamatDomisili.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "provinsi_domisili" to member.provinsiDomisili.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kota_kabupaten_domisili" to member.kotaKabupatenDomisili.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kecamatan_domisili" to member.kecamatanDomisili.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kelurahan_domisili" to member.kelurahanDomisili.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "kode_pos_domisili" to member.kodePosDomisili.toRequestBody("text/plain".toMediaTypeOrNull())
                )

                val ktpPart = member.ktpPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("ktp_file", file.name, requestFile)
                    } else null
                }
                val ktpSecondaryPart = member.ktpSecondaryPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("ktp_file_secondary", file.name, requestFile)
                    } else null
                }

                val response = repository.uploadMember(token, partMap, ktpPart, ktpSecondaryPart)
                if (response.isSuccessful) {
                    repository.saveMember(member.copy(registrationStatus = "uploaded"))
                    _uploadStatus.update { "Success upload ${member.name}" }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _uploadStatus.update { "Failed upload ${member.name}: ${response.code()} ${response.message()}\n$errorBody" }
                }
            } catch (e: Exception) {
                _uploadStatus.update { "Error: ${e.message}" }
            }
        }
    }

    fun uploadAllDrafts(token: String) {
        viewModelScope.launch {
            val drafts = repository.getDrafts().first()
            if (drafts.isEmpty()) {
                _uploadStatus.update { "No drafts to upload" }
                return@launch
            }
            drafts.forEach { draft ->
                uploadMember(token, draft)
            }
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.update { false }
    }
    
    fun clearUploadStatus() {
        _uploadStatus.update { null }
    }
}
