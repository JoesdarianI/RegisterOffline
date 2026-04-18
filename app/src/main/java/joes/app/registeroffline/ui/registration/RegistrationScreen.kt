package joes.app.registeroffline.ui.registration

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val phone by viewModel.phone.collectAsState()
    val nik by viewModel.nik.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val birthPlace by viewModel.birthPlace.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val maritalStatus by viewModel.maritalStatus.collectAsState()
    val job by viewModel.job.collectAsState()
    val address by viewModel.address.collectAsState()
    val province by viewModel.province.collectAsState()
    val city by viewModel.city.collectAsState()
    val district by viewModel.district.collectAsState()
    val subDistrict by viewModel.subDistrict.collectAsState()
    val postalCode by viewModel.postalCode.collectAsState()
    val isDomisiliSame by viewModel.isDomisiliSame.collectAsState()
    val ktpPath by viewModel.ktpPath.collectAsState()
    val ktpSecondaryPath by viewModel.ktpSecondaryPath.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var showImageSourceDialog by remember { mutableStateOf<ImageSourceTarget?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers for Gallery
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            when (showImageSourceDialog) {
                ImageSourceTarget.KTP -> viewModel.handleKtpImage(context, it)
                ImageSourceTarget.KTP_SECONDARY -> viewModel.handleKtpSecondaryImage(context, it)
                null -> {}
            }
        }
        showImageSourceDialog = null
    }

    // Launchers for Camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            when (showImageSourceDialog) {
                ImageSourceTarget.KTP -> viewModel.handleKtpImage(tempCameraUri!!.toString())
                ImageSourceTarget.KTP_SECONDARY -> viewModel.handleKtpSecondaryImage(tempCameraUri!!.toString())
                null -> {}
            }
        }
        showImageSourceDialog = null
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Draft berhasil disimpan", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveSuccess()
            onBack()
        }
    }

    if (showImageSourceDialog != null) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = null },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Ambil foto dari kamera atau pilih dari galeri?") },
            confirmButton = {
                TextButton(onClick = {
                    val uri = viewModel.createImageUri(context)
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Data", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Not used here */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5), contentColor = Color.LightGray),
                    enabled = false
                ) {
                    Text("Upload")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.onSaveDraft() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("Simpan sebagai Draft", color = Color(0xFF1A237E))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionTitle("Data Utama")
            
            InfoBox("Nomor Handphone, NIK, Foto KTP, dan Foto Diri wajib diisi sebelum disimpan / di-upload")
            
            FormLabel("Nomor Handphone", true)
            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                placeholder = { Text("Masukkan nomor handphone", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            
            FormLabel("NIK", true)
            OutlinedTextField(
                value = nik,
                onValueChange = { viewModel.onNikChange(it) },
                placeholder = { Text("16 digit no KTP", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            
            FormLabel("Foto KTP", true)
            Text("Ambil 2 foto KTP untuk hasil yang lebih baik. Pastikan KTP terlihat jelas and tidak blur.", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PhotoPlaceholder(
                    uri = ktpPath,
                    onClick = { showImageSourceDialog = ImageSourceTarget.KTP },
                    modifier = Modifier.weight(1f)
                )
                PhotoPlaceholder(
                    uri = ktpSecondaryPath,
                    onClick = { showImageSourceDialog = ImageSourceTarget.KTP_SECONDARY },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Informasi Lainnya")
            
            FormLabel("Nama Lengkap", false)
            OutlinedTextField(
                value = fullName,
                onValueChange = { viewModel.onFullNameChange(it) },
                placeholder = { Text("Masukkan nama sesuai KTP", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            FormLabel("Tempat Lahir", false)
            OutlinedTextField(
                value = birthPlace,
                onValueChange = { viewModel.onBirthPlaceChange(it) },
                placeholder = { Text("Masukkan tempat lahir sesuai KTP", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            FormLabel("Tanggal Lahir", false)
            OutlinedTextField(
                value = birthDate,
                onValueChange = { viewModel.onBirthDateChange(it) },
                placeholder = { Text("DD/MM/YYYY", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
            )

            FormLabel("Jenis Kelamin", false)
            DropdownField(
                value = gender,
                onValueChange = { viewModel.onGenderChange(it) },
                placeholder = "Pilih jenis kelamin",
                options = listOf("Laki-laki", "Perempuan")
            )

            FormLabel("Status", false)
            DropdownField(
                value = maritalStatus,
                onValueChange = { viewModel.onMaritalStatusChange(it) },
                placeholder = "Pilih status sesuai KTP",
                options = listOf("Belum Kawin", "Kawin", "Cerai Hidup", "Cerai Mati")
            )

            FormLabel("Pekerjaan", false)
            DropdownField(
                value = job,
                onValueChange = { viewModel.onJobChange(it) },
                placeholder = "Pilih pekerjaan sesuai KTP",
                options = listOf("Karyawan Swasta", "PNS", "Wiraswasta", "Pelajar/Mahasiswa", "Lainnya")
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Informasi Alamat KTP")

            FormLabel("Alamat Lengkap", false)
            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.onAddressChange(it) },
                placeholder = { Text("Masukkan alamat sesuai KTP", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            FormLabel("Provinsi", false)
            DropdownField(
                value = province,
                onValueChange = { viewModel.onProvinceChange(it) },
                placeholder = "Pilih Provinsi",
                options = listOf("DKI Jakarta", "Jawa Barat", "Jawa Tengah", "Jawa Timur", "Banten")
            )

            FormLabel("Kota/Kabupaten", false)
            DropdownField(
                value = city,
                onValueChange = { viewModel.onCityChange(it) },
                placeholder = "Pilih Kota/Kabupaten",
                options = listOf("Jakarta Pusat", "Jakarta Selatan", "Jakarta Utara", "Jakarta Barat", "Jakarta Timur")
            )

            FormLabel("Kecamatan", false)
            DropdownField(
                value = district,
                onValueChange = { viewModel.onDistrictChange(it) },
                placeholder = "Pilih Kecamatan",
                options = listOf("Menteng", "Gambir", "Senen", "Cempaka Putih")
            )

            FormLabel("Kelurahan", false)
            DropdownField(
                value = subDistrict,
                onValueChange = { viewModel.onSubDistrictChange(it) },
                placeholder = "Pilih Kelurahan",
                options = listOf("Menteng", "Pegangsaan", "Cikini", "Kebon Sirih")
            )

            FormLabel("Kode Pos", false)
            OutlinedTextField(
                value = postalCode,
                onValueChange = { viewModel.onPostalCodeChange(it) },
                placeholder = { Text("Masukkan Kode Pos", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Alamat Domisili")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDomisiliSame, onCheckedChange = { viewModel.onDomisiliSameChange(it) })
                Text("Alamat domisili sama dengan alamat pada KTP", fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

enum class ImageSourceTarget {
    KTP, KTP_SECONDARY
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A237E),
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun FormLabel(label: String, isRequired: Boolean) {
    Text(
        text = buildAnnotatedString {
            append(label)
            if (isRequired) {
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append(" *")
                }
            }
        },
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun InfoBox(text: String) {
    Surface(
        color = Color(0xFFE8EAF6),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF3F51B5), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 12.sp, color = Color.Black)
        }
    }
}

@Composable
fun PhotoPlaceholder(uri: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(120.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF1A237E), modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun DropdownField(value: String, onValueChange: (String) -> Unit, placeholder: String, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.LightGray,
                disabledPlaceholderColor = Color.LightGray,
                disabledTrailingIconColor = Color.Gray
            ),
            trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
