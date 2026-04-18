package joes.app.registeroffline.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val nik: String,
    val phone: String,
    val birthPlace: String,
    val birthDate: String,
    val status: String,
    val occupation: String,
    val address: String,
    val provinsi: String,
    val kotaKabupaten: String,
    val kecamatan: String,
    val kelurahan: String,
    val kodePos: String,
    val alamatDomisili: String,
    val provinsiDomisili: String,
    val kotaKabupatenDomisili: String,
    val kecamatanDomisili: String,
    val kelurahanDomisili: String,
    val kodePosDomisili: String,
    val ktpPath: String? = null,
    val ktpSecondaryPath: String? = null,
    val registrationStatus: String = "drafted" // "drafted" or "uploaded"
)
