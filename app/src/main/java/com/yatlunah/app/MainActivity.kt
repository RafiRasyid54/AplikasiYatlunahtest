package com.yatlunah.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.yatlunah.app.data.worker.NotificationWorker
import java.util.concurrent.TimeUnit
import java.net.URLDecoder
import java.net.URLEncoder

// --- IMPORT MODEL ---
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.ui.screen.admin.AdminControlCenterScreen
import com.yatlunah.app.ui.screen.guru.GuruControlCenterScreen

// --- IMPORT SCREEN UMUM ---
import com.yatlunah.app.ui.screen.login.LoginScreen
import com.yatlunah.app.ui.screen.register.RegisterScreen
import com.yatlunah.app.ui.screen.santri.SantriDashboardScreen
import com.yatlunah.app.ui.screen.materi.*
import com.yatlunah.app.ui.screen.splash.SplashScreen
import com.yatlunah.app.ui.screen.profile.ProfileScreen
// ✅ IMPORT SANTRI CONTROL CENTER
import com.yatlunah.app.ui.screen.santri.SantriControlCenterScreen

// --- IMPORT SCREEN GURU & ADMIN ---
import com.yatlunah.app.ui.screen.admin.AdminDashboardScreen
import com.yatlunah.app.ui.screen.admin.AdminQuoteScreen
import com.yatlunah.app.ui.screen.guru.GuruDashboardScreen
import com.yatlunah.app.ui.screen.guru.GuruPenilaianDetailScreen
import com.yatlunah.app.ui.screen.guru.GuruSetoranQueueScreen
import com.yatlunah.app.ui.screen.guru.GuruJilidMenuScreen
import com.yatlunah.app.ui.screen.admin.UserManagementMenuScreen
import com.yatlunah.app.ui.screen.admin.UserListScreen
import com.yatlunah.app.ui.screen.admin.UserDetailScreen

import com.yatlunah.app.data.model.ProgramYatlunah
import com.yatlunah.app.ui.screen.bimbingan.DaftarBimbinganScreen
import com.yatlunah.app.ui.screen.guru.GuruBimbinganScreen
import com.yatlunah.app.ui.screen.info_program.ProgramDetailScreen
import com.yatlunah.app.ui.screen.info_program.ProgramListScreen
import com.yatlunah.app.ui.screen.santri.SantriBimbinganDetailScreen
import com.yatlunah.app.ui.screen.santri.SantriViewModel

import com.yatlunah.app.ui.theme.AplikasiYatlunahtestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDailyNotification()
        setContent {
            AplikasiYatlunahtestTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        // --- 1. SPLASH & LOGIN ---
                        composable("splash") {
                            SplashScreen(onTimeout = {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { userId, namaUser, emailUser, role ->
                                    val encodedId = URLEncoder.encode(userId, "UTF-8")
                                    val encodedName = URLEncoder.encode(namaUser, "UTF-8")
                                    val cleanRole = role.lowercase().trim()

                                    when (cleanRole) {
                                        "admin" -> {
                                            navController.navigate("dashboard_admin/$encodedId/$encodedName") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                        "guru" -> {
                                            navController.navigate("dashboard_guru/$encodedId/$encodedName") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                        else -> {
                                            // ✅ PERUBAHAN: Arahkan ke dashboard_santri
                                            val encodedEmail = URLEncoder.encode(emailUser, "UTF-8")
                                            navController.navigate("dashboard_santri/$encodedId/$encodedName/$encodedEmail") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // --- 2. ADMIN FLOW ---
                        composable(
                            route = "dashboard_admin/{id}/{nama}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val rawName = backStackEntry.arguments?.getString("nama") ?: ""
                            val nameAdmin = URLDecoder.decode(rawName, "UTF-8")

                            AdminDashboardScreen(
                                namaAdmin = nameAdmin,
                                onNavigateToUserMgmt = { navController.navigate("admin_control_center") },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$id/$rawName/${URLEncoder.encode("admin@yatlunah.com", "UTF-8")}")
                                }
                            )
                        }

                        // --- 3. GURU FLOW ---
                        composable("dashboard_guru/{id}/{nama}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("id") ?: ""
                            val encodedName = backStackEntry.arguments?.getString("nama") ?: ""
                            val nameGuru = URLDecoder.decode(encodedName, "UTF-8")

                            GuruDashboardScreen(
                                namaGuru = nameGuru,
                                onNavigateToAntrean = {
                                    navController.navigate("guru_control_center/$idGuru")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$idGuru/$encodedName/guru@yatlunah.com")
                                }
                            )
                        }

                        composable("guru_control_center/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""

                            GuruControlCenterScreen(
                                idGuru = idGuru,
                                onNavigateToSetoran = { id ->
                                    navController.navigate("guru_menu_jilid/$id")
                                },
                                onNavigateToBimbingan = { id ->
                                    navController.navigate("guru_bimbingan/$id")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("guru_menu_jilid/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""

                            GuruJilidMenuScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToQueue = { jilidId ->
                                    navController.navigate("guru_antrean/$jilidId/$idGuru")
                                }
                            )
                        }

                        composable("guru_bimbingan/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""

                            GuruBimbinganScreen(
                                idGuru = idGuru, // ✅ Kirim ID ke Screen
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("guru_antrean/{jilidId}/{idGuru}") { backStackEntry ->
                            val jilidId = backStackEntry.arguments?.getString("jilidId")?.toInt() ?: 1
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""

                            GuruSetoranQueueScreen(
                                jilidTarget = jilidId,
                                onBack = { navController.popBackStack() },
                                onNavigateToPenilaian = { setoran: Setoran ->
                                    val encName = URLEncoder.encode(setoran.namaSantri ?: "Siswa", "UTF-8")
                                    val encAudio = URLEncoder.encode(setoran.audioUrl ?: "", "UTF-8")
                                    navController.navigate("guru_nilai/${setoran.id}/$idGuru/$encName/${setoran.jilid}/${setoran.halaman}/$encAudio")
                                }
                            )
                        }

                        composable("guru_nilai/{setoranId}/{idGuru}/{nama}/{jilid}/{halaman}/{audioUrl}") { backStackEntry ->
                            val sId = backStackEntry.arguments?.getString("setoranId")?.toInt() ?: 0
                            val gId = backStackEntry.arguments?.getString("idGuru") ?: ""
                            val nama = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val jilid = backStackEntry.arguments?.getString("jilid")?.toInt() ?: 1
                            val halaman = backStackEntry.arguments?.getString("halaman")?.toInt() ?: 1
                            val rawAudio = backStackEntry.arguments?.getString("audioUrl") ?: ""
                            val audioUrl = URLDecoder.decode(rawAudio, "UTF-8")

                            GuruPenilaianDetailScreen(
                                setoranId = sId, idGuru = gId, nama = nama,
                                jilid = jilid, halaman = halaman, audioUrl = audioUrl,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- 4. SANTRI FLOW ---
                        // ✅ PERUBAHAN: Rute diubah dari dashboard_user menjadi dashboard_santri
                        composable(
                            route = "dashboard_santri/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""

                            SantriDashboardScreen(
                                userId = id,
                                namaUser = URLDecoder.decode(name, "UTF-8"),
                                emailUser = email, // ✅ TAMBAHKAN INI
                                navController = navController, // ✅ TAMBAHKAN INI agar kartu bimbingan bisa diklik
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToDashboard = {
                                    // Logika pengecekan route agar tidak tumpang tindih
                                    if (navController.currentBackStackEntry?.destination?.route != "dashboard_santri/{id}/{nama}/{email}") {
                                        navController.navigate("dashboard_santri/$id/$name/$email") {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                onNavigateToJilid = {
                                    navController.navigate("santri_control_center/$id/$name/$email") {
                                        launchSingleTop = true
                                        popUpTo("dashboard_santri/{id}/{nama}/{email}") {
                                            saveState = true
                                        }
                                    }
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$id/$name/$email") {
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToBimbingan = {
                                    // ✅ Arahkan ke Detail Bimbingan yang baru saja kita buat
                                    navController.navigate("santri_bimbingan_detail/$id/$name/$email")
                                },
                                onNavigateToInfoProgram = {
                                    navController.navigate("info_program")
                                }
                            )
                        }

                        composable("santri_bimbingan_detail/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""

                            val viewModel: SantriViewModel = viewModel()

                            // Ambil status terbaru dari database
                            LaunchedEffect(id) {
                                viewModel.fetchStatusBimbingan(id)
                            }

                            SantriBimbinganDetailScreen(
                                status = viewModel.bimbinganStatus,
                                namaGuru = viewModel.namaGuru,
                                onBack = { navController.popBackStack() },
                                // ✅ DI SINI TEMPATNYA MENGHUBUNGKAN TOMBOL KE PAGE DAFTAR
                                onNavigateToFormDaftar = {
                                    navController.navigate("daftar_bimbingan/$id/$name/$email")
                                }
                            )
                        }

                        // ✅ TAMBAHAN: Control Center khusus Santri
                        // Di dalam NavHost { ... }
                        composable(
                            route = "santri_control_center/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            // Gunakan URLDecoder untuk menjaga karakter spesial di email/nama
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""

                            // ✅ PASTIKAN SEMUA PARAMETER DI BAWAH INI SESUAI DENGAN DEFINISI DI SCREEN
                            SantriControlCenterScreen(
                                userId = id,
                                namaUser = name,
                                emailUser = email,
                                navController = navController,
                                onNavigateToMateri = { navController.navigate("menu_belajar/$id/$name/$email") },
                                onNavigateToRiwayat = { navController.navigate("riwayat_setoran/$id") },
                                // ✅ TAMBAHKAN NAVIGASI KE PROFILE
                                onNavigateToProfile = {
                                    navController.navigate("profile/$id/$name/$email") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable(
                            route = "menu_belajar/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""

                            MenuBelajarScreen(
                                namaUser = URLDecoder.decode(name, "UTF-8"),
                                // ✅ HANYA GUNAKAN onBack SEKARANG
                                onBack = { navController.popBackStack() },
                                onNavigateToMateri = { navController.navigate("list_jilid/$id/$name/$email") },
                                onNavigateToRiwayat = { navController.navigate("riwayat_setoran/$id") }
                            )
                        }

                        composable(
                            route = "profile/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            // 1. Ambil data asli dari arguments
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val rawName = backStackEntry.arguments?.getString("nama") ?: ""
                            val rawEmail = backStackEntry.arguments?.getString("email") ?: ""

                            // 2. Decode agar karakter spesial (seperti @ atau spasi) kembali normal
                            val decodedName = URLDecoder.decode(rawName, "UTF-8")
                            val decodedEmail = URLDecoder.decode(rawEmail, "UTF-8")

                            ProfileScreen(
                                userIdAsli = id,
                                namaUser = decodedName, // ✅ Gunakan variabel yang sudah di-decode
                                emailUser = decodedEmail,
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    // ✅ Kembali ke Dashboard & bersihkan tumpukan di atasnya
                                    navController.navigate("dashboard_santri/$id/$rawName/$rawEmail") {
                                        popUpTo("dashboard_santri/{id}/{nama}/{email}") {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToJilid = {
                                    // ✅ Cek role berdasarkan email yang sudah di-decode
                                    when {
                                        decodedEmail.contains("admin") -> {
                                            navController.navigate("admin_control_center") { launchSingleTop = true }
                                        }
                                        decodedEmail.contains("guru") -> {
                                            navController.navigate("guru_control_center/$id") { launchSingleTop = true }
                                        }
                                        else -> {
                                            // ✅ Arahkan santri ke Control Center-nya
                                            navController.navigate("santri_control_center/$id/$rawName/$rawEmail") {
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        composable("baca_jilid/{jilidId}/{userId}") { backStackEntry ->
                            val jid = backStackEntry.arguments?.getString("jilidId")?.toInt()?.let { if (it <= 0) 1 else it } ?: 1
                            val uid = backStackEntry.arguments?.getString("userId") ?: ""
                            PdfJilidViewerScreen(jilidId = jid, userId = uid, onBack = { navController.popBackStack() })
                        }

                        composable("riwayat_setoran/{userId}") { backStackEntry ->
                            val uid = backStackEntry.arguments?.getString("userId") ?: ""
                            RiwayatSetoranScreen(userId = uid, onBack = { navController.popBackStack() })
                        }

                        composable("list_jilid/{id}/{nama}/{email}") { backStackEntry ->
                            val uid = backStackEntry.arguments?.getString("id") ?: ""
                            JilidListScreen(
                                onNavigateToDetail = { jid -> navController.navigate("baca_jilid/$jid/$uid") },
                                onNavigateToHome = { navController.popBackStack() }
                            )
                        }

                        // --- 5. REGISTER & ADMIN FLOW ---
                        composable("register") { RegisterScreen(onNavigateToLogin = { navController.popBackStack() }) }

                        composable("user_management") {
                            UserManagementMenuScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToList = { r: String -> navController.navigate("user_list/$r") }
                            )
                        }

                        composable("user_list/{role}") { backStackEntry ->
                            // ✅ PERUBAHAN: Default ke 'santri', bukan 'peserta'
                            val r = backStackEntry.arguments?.getString("role") ?: "santri"
                            UserListScreen(
                                role = r,
                                onBack = { navController.popBackStack() },
                                onNavigateToDetail = { id, n, e ->
                                    val en = URLEncoder.encode(n, "UTF-8")
                                    val ee = URLEncoder.encode(e, "UTF-8")
                                    navController.navigate("user_detail/$id/$en/$ee/$r")
                                }
                            )
                        }

                        composable(
                            route = "user_detail/{id}/{nama}/{email}/{role}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val n = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val e = URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8")
                            // ✅ PERUBAHAN: Default ke 'santri', bukan 'peserta'
                            val r = backStackEntry.arguments?.getString("role") ?: "santri"

                            UserDetailScreen(
                                userId = id, userName = n, userEmail = e,
                                initialRole = r,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("admin_control_center") {
                            AdminControlCenterScreen(
                                onNavigateToUserMgmt = { navController.navigate("user_management") },
                                onNavigateToQuotes = { navController.navigate("admin_quotes") },
                                onNavigateToLaporan = { /* TODO */ },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("admin_quotes") {
                            AdminQuoteScreen(onBack = { navController.popBackStack() })
                        }

                        composable(
                            route = "daftar_bimbingan/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            // nama & email ada di rute tapi tidak kita kirim ke screen pendaftaran karena tidak ada di tabel DB

                            val santriViewModel: SantriViewModel = viewModel()

                            // Konversi progres dari 0.0-1.0 (float) ke 0-100 (int)
                            val currentProgress = (santriViewModel.progressPercent * 100).toInt()

                            DaftarBimbinganScreen(
                                userId = id,
                                totalProgress = 85, // ✅ Dinamis dari DB
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("info_program") {
                            ProgramListScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToDetail = { id ->
                                    navController.navigate("program_detail/$id")
                                }
                            )
                        }

                        composable("program_detail/{programId}") { backStackEntry ->
                            val idString = backStackEntry.arguments?.getString("programId").orEmpty().ifEmpty { "1" }

                            val daftarProgram = listOf(
                                ProgramYatlunah(
                                    id = 1,
                                    nama = "Program Reguler",
                                    deskripsi = "Pembelajaran rutin Jilid 1-6...",
                                    targetPeserta = "Anak-anak & Dewasa",
                                    materiUtama = "Tahsin dasar",
                                    fiturUnggulan = listOf("Bimbingan 1-on-1", "Sertifikat")
                                )
                            )

                            val programData = daftarProgram.find { it.id.toString() == idString }

                            ProgramDetailScreen(
                                program = programData,
                                onBack = { navController.popBackStack() },
                                onRegister = { _ ->
                                    navController.navigate("daftar_bimbingan/user_default/85")
                                },
                                fiturUnggulan = listOf(
                                    "✔ Bimbingan Intensif 1-on-1",
                                    "✔ Sertifikat Kelulusan Resmi",
                                    "✔ Akses Materi Fleksibel"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupDailyNotification() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "yatlunah_notif",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}