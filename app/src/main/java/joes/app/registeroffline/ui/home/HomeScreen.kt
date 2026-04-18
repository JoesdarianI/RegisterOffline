package joes.app.registeroffline.ui.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import joes.app.registeroffline.R
import joes.app.registeroffline.data.model.Member
import joes.app.registeroffline.data.model.User
import joes.app.registeroffline.ui.login.LoginViewModel
import joes.app.registeroffline.ui.registration.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profile: User?,
    loginViewModel: LoginViewModel,
    registrationViewModel: RegistrationViewModel,
    onTambahDataClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val token by loginViewModel.token.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Draft", "Sudah Di-Upload")
    
    val drafts by registrationViewModel.getDrafts().collectAsState(initial = emptyList())
    val uploaded by registrationViewModel.getUploadedMembers().collectAsState(initial = emptyList())
    val uploadStatus by registrationViewModel.uploadStatus.collectAsState()

    var showUploadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uploadStatus) {
        uploadStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            registrationViewModel.clearUploadStatus()
        }
    }

    if (showUploadDialog) {
        ModalBottomSheet(
            onDismissRequest = { showUploadDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showUploadDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = "Upload Semua Data",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Image(
                    painter = painterResource(id = R.drawable.upload_state_illustration), // Reusing the same illustration for upload prompt
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Apakah kamu yakin ingin upload semua data?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Pastikan kamu sudah mengisi semua data yang diperlukan dengan benar, ya!",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        token?.let { registrationViewModel.uploadAllDrafts(it) }
                        showUploadDialog = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Text("Ya, Upload Semua (${drafts.size})", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = { showUploadDialog = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF1A237E))
                ) {
                    Text("Batal", color = Color(0xFF1A237E))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = Color(0xFF1A237E),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Register Offline",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onProfileClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = profile?.name ?: "User",
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color(0xFF1A237E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                    onClick = onTambahDataClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Data", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showUploadDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (drafts.isNotEmpty()) Color(0xFF1A237E) else Color.LightGray),
                    enabled = drafts.isNotEmpty() && token != null
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = if (drafts.isNotEmpty()) Color(0xFF1A237E) else Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload Semua (${drafts.size})",
                        color = if (drafts.isNotEmpty()) Color(0xFF1A237E) else Color.LightGray
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF1A237E),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF1A237E)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF1A237E) else Color.Gray
                            )
                        }
                    )
                }
            }

            if (selectedTab == 0) {
                if (drafts.isEmpty()) {
                    EmptyState(message = "Belum ada data", subMessage = "Klik \"Tambah Data\" untuk menambahkan data calon anggota")
                } else {
                    DraftList(drafts) { member ->
                        token?.let { registrationViewModel.uploadMember(it, member) }
                    }
                }
            } else {
                if (uploaded.isEmpty()) {
                    EmptyState(message = "Belum ada data terupload", subMessage = "Data yang berhasil disinkronkan akan muncul di sini")
                } else {
                    DraftList(uploaded) { /* Already uploaded */ }
                }
            }
        }
    }
}

@Composable
fun DraftList(drafts: List<Member>, onUpload: (Member) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "List Draft KTA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Upload untuk mengirimkan data ini ke admin untuk di-verifikasi.", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(drafts) { draft ->
            DraftItem(index = drafts.indexOf(draft) + 1 ,draft, onUpload)
        }
    }
}

@Composable
fun DraftItem(
    index: Int,
    member: Member, onUpload: (Member) -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = index.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                if (member.ktpPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(member.ktpPath),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp, 40.dp).background(Color.LightGray, RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = member.nik, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = member.phone, fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (member.registrationStatus == "drafted") Color(0xFFFFF9C4) else Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (member.registrationStatus == "drafted") "Draft" else "Di-Upload",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = if (member.registrationStatus == "drafted") Color(0xFFFBC02D) else Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (member.registrationStatus == "drafted") {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { /* Edit */ }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit")
                    }
                    VerticalDivider(modifier = Modifier.height(32.dp).padding(vertical = 8.dp))
                    TextButton(onClick = { onUpload(member) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload")
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(modifier = modifier.width(1.dp).background(Color(0xFFF5F5F5)))
}

@Composable
fun EmptyState(message: String, subMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_state_illustration), 
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subMessage,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
