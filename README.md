# Register Offline

Register Offline is a robust Android application built with modern development practices to facilitate member registration in environments with intermittent internet connectivity. It allows users to capture data and photos offline, store them locally, and synchronize them with a central API when a connection is available.

## 🚀 Features

- **Persistent Authentication**: Securely stores user sessions in Room, allowing automatic login upon app restart.
- **Offline First (Drafting)**: All registration data is saved locally to a Room database first.
- **Advanced Registration Form**:
    - Comprehensive member data capture (NIK, Phone, Address, etc.).
    - Dual KTP image support (Primary & Secondary).
    - Intelligent "Same as KTP" address mapping.
- **Smart Image Handling**:
    - Capture photos via Camera or select from Gallery.
    - Automatic persistence to app-internal storage (fixes permission expiration).
    - **JPEG Compression (70% quality)** to ensure fast uploads on mobile networks.
- **Data Synchronization**:
    - Individual member upload from Home screen.
    - Bulk "Upload Semua" functionality with confirmation Modal Bottom Sheet.
    - Real-time status tracking (Draft vs. Di-Upload).
- **Network Stability**: Forced HTTP/1.1 protocol to ensure compatibility with specific API gateways and prevent protocol hangs.

## 🏗️ Architecture (MVVM + Repository)

The project follows a clean, modular architecture:

### 1. Data Layer (`joes.app.registeroffline.data`)
- **`model`**: Contains Data Classes for Auth (Login/Profile) and the Member Entity.
- **`local`**: Room Database (`AppDatabase`), DAOs for Users and Members.
- **`remote`**: Retrofit API Service definition and the specialized `RetrofitClient` with logging and timeout configurations.
- **`repository`**: The Single Source of Truth. It coordinates between the Room database and the Retrofit API.

### 2. UI Layer (`joes.app.registeroffline.ui`)
- **Splash**: Branded entry screen.
- **Login**: Handles token acquisition and profile fetching.
- **Home**: Dashboard showing Local Drafts and Uploaded members using a Tabbed interface.
- **Registration**: Complex scrollable form for data entry and image processing.
- **Profile**: User session management and logout.

### 3. Navigation
Managed in `MainActivity.kt` using a state-driven approach with `LaunchedEffect` for automatic transitions based on authentication status.

## 🛠️ Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Local DB**: Room
- **Networking**: Retrofit 2 & OkHttp 4
- **Image Loading**: Coil
- **Dependency Injection**: Manual injection via ViewModelProvider Factories.

## ⚙️ Configuration

### Network
The API is configured to target: `https://api-test.partaiperindo.com/`
- **Timeouts**: 60s (Connect/Read/Write)
- **Protocol**: Forced `HTTP/1.1`

### Permissions
The app uses the following permissions:
- `INTERNET`: For API communication.
- `CAMERA`: For capturing KTP photos (using FileProvider for secure URI sharing).

## 📥 How to Run
1. Clone the repository.
2. Ensure you have the latest Android Studio installed.
3. Add an image named `empty_state_illustration.png` to `app/src/main/res/drawable/`.
4. Sync Gradle and run the `:app` module.

---
*Built with expert Android practices for the Partai Perindo registration flow.*
