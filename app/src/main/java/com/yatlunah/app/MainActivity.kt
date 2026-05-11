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
import com.yatlunah.app.ui.screen.SplashScreen
import com.yatlunah.app.ui.screen.profile.ProfileScreen
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
import com.yatlunah.app.ui.screen.latihan.LatihanMakhrajScreen
import com.yatlunah.app.ui.screen.santri.SantriBimbinganDetailScreen
import com.yatlunah.app.ui.screen.santri.SantriViewModel

import com.yatlunah.app.ui.theme.AplikasiYatlunahtestTheme

import android.widget.Toast // Untuk error 'Toast'
import androidx.compose.runtime.rememberCoroutineScope // Untuk 'rememberCoroutineScope'
import androidx.compose.ui.platform.LocalContext // Untuk 'LocalContext'
import kotlinx.coroutines.launch // Untuk scope.launch
import com.yatlunah.app.ui.screen.admin.InputLatihanScreen // Untuk 'InputLatihanScreen'
import com.yatlunah.app.data.remote.RetrofitClient // Untuk 'RetrofitClient'
import com.yatlunah.app.data.model.LatihanSoal // Untuk tipe data LatihanSoal

import com.yatlunah.app.ui.screen.latihan.LatihanViewModel
import com.yatlunah.app.ui.screen.latihan.LatihanMakhrajScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yatlunah.app.ui.screen.admin.QuestionMonitoringScreen
import com.yatlunah.app.ui.screen.admin_mitra.AdminMitraDashboardScreen
import com.yatlunah.app.ui.screen.admin_mitra.MitraControlScreen
import com.yatlunah.app.ui.screen.admin_mitra.MitraUserListScreen

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
                        // 1. UBAH startDestination ke splash
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(onTimeout = {
                                // 2. LOGIKA GUEST: Jika tidak ada session, langsung ke Dashboard Santri
                                // Kita gunakan ID "guest_user" sebagai penanda
                                val guestId = "guest_user"
                                val guestName = URLEncoder.encode("Tamu Yatlunah", "UTF-8")
                                val guestEmail = URLEncoder.encode("guest@yatlunah.id", "UTF-8")

                                navController.navigate("dashboard_santri/$guestId/$guestName/$guestEmail/santri") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        composable("register") {
                            RegisterScreen(
                                // PERBAIKAN: Gunakan navigate("login") daripada popBackStack agar pasti ke halaman Login
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onRegisterSucces = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                // PERBAIKAN 1: Tambahkan parameter kelima (idMitra) di sini
                                onLoginSuccess = { userId, namaUser, emailUser, role, idMitra ->
                                    val encodedId = URLEncoder.encode(userId, "UTF-8")
                                    val encodedName = URLEncoder.encode(namaUser, "UTF-8")
                                    val encodedEmail = URLEncoder.encode(emailUser, "UTF-8")
                                    val cleanRole = role.lowercase().trim()

                                    // PERBAIKAN 2: Encode idMitra (berikan default "0" atau "null" jika kosong)
                                    val encodedIdMitra = if (!idMitra.isNullOrEmpty()) URLEncoder.encode(idMitra, "UTF-8") else "0"

                                    val route = when (cleanRole) {
                                        "admin" -> "dashboard_admin/$encodedId/$encodedName/$encodedEmail/$cleanRole"

                                        // PERBAIKAN 3: Tambahkan pengecekan role untuk admin mitra
                                        "admin_mitra", "mitra" -> "dashboard_mitra/$encodedId/$encodedName/$encodedEmail/$cleanRole/$encodedIdMitra"

                                        "guru" -> "dashboard_guru/$encodedId/$encodedName/$encodedEmail/$cleanRole"
                                        else -> "dashboard_santri/$encodedId/$encodedName/$encodedEmail/$cleanRole"
                                    }

                                    navController.navigate(route) {
                                        // PERBAIKAN KRUSIAL: Hapus Dashboard Guest dari history
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        // --- ADMIN FLOW ---
                        composable(
                            route = "dashboard_admin/{id}/{nama}/{email}/{role}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val role = backStackEntry.arguments?.getString("role") ?: "admin"

                            AdminDashboardScreen(
                                namaAdmin = URLDecoder.decode(name, "UTF-8"),
                                onNavigateToUserMgmt = { navController.navigate("admin_control_center") },
                                onNavigateToProfile = { navController.navigate("profile/$id/$name/$email/$role") }
                            )
                        }

                        // --- GURU FLOW ---
                        composable(
                            route = "dashboard_guru/{id}/{nama}/{email}/{role}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val role = backStackEntry.arguments?.getString("role") ?: "guru"

                            GuruDashboardScreen(
                                namaGuru = URLDecoder.decode(name, "UTF-8"),
                                onNavigateToAntrean = { navController.navigate("guru_control_center/$id") },
                                onNavigateToProfile = { navController.navigate("profile/$id/$name/$email/$role") }
                            )
                        }

                        composable("guru_control_center/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""
                            GuruControlCenterScreen(
                                idGuru = idGuru,
                                onNavigateToSetoran = { id -> navController.navigate("guru_menu_jilid/$id") },
                                onNavigateToBimbingan = { id -> navController.navigate("guru_bimbingan/$id") },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("guru_menu_jilid/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""
                            GuruJilidMenuScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToQueue = { jilidId -> navController.navigate("guru_antrean/$jilidId/$idGuru") }
                            )
                        }

                        composable("guru_bimbingan/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""
                            GuruBimbinganScreen(idGuru = idGuru, onBack = { navController.popBackStack() })
                        }

                        composable("guru_antrean/{jilidId}/{idGuru}") { backStackEntry ->
                            val jilidId = backStackEntry.arguments?.getString("jilidId")?.toInt() ?: 1
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""
                            GuruSetoranQueueScreen(
                                jilidTarget = jilidId,
                                onBack = { navController.popBackStack() },
                                onNavigateToPenilaian = { setoran ->
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
                            val audioUrl = URLDecoder.decode(backStackEntry.arguments?.getString("audioUrl") ?: "", "UTF-8")

                            GuruPenilaianDetailScreen(
                                setoranId = sId, idGuru = gId, nama = nama,
                                jilid = jilid, halaman = halaman, audioUrl = audioUrl,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        // Mitra Flow
                        composable(
                            route = "dashboard_mitra/{id}/{nama}/{email}/{role}/{idMitra}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType },
                                navArgument("idMitra") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val email = URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8")
                            val role = backStackEntry.arguments?.getString("role") ?: "mitra"
                            AdminMitraDashboardScreen(
                                namaAdmin = name,
                                onNavigateToControl = { navController.navigate("mitra_control") },
                                onNavigateToUserList = { targetRole -> navController.navigate("mitra_user_list/$targetRole") },
                                onNavigateToProfile = { navController.navigate("profile/$id/${URLEncoder.encode(name, "UTF-8")}/${URLEncoder.encode(email, "UTF-8")}/$role") },
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
                            )
                        }

                        composable("mitra_control") {
                            MitraControlScreen(
                                onNavigateToUserList = { targetRole -> navController.navigate("mitra_user_list/$targetRole") },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("mitra_user_list/{role}") { backStackEntry ->
                            val currentRole = backStackEntry.arguments?.getString("role") ?: "santri"
                            MitraUserListScreen(
                                role = currentRole,
                                onBack = { navController.popBackStack() },
                                onNavigateToDetail = { userId, n, e ->
                                    val safeId = if (userId.isNullOrEmpty()) "0" else userId
                                    val en = URLEncoder.encode(n ?: "User", "UTF-8")
                                    val ee = URLEncoder.encode(e ?: "", "UTF-8")

                                    // Pastikan currentRole diteruskan ke user_detail
                                    navController.navigate("user_detail/$safeId/$en/$ee/$currentRole")
                                }
                            )
                        }
                        // --- SANTRI FLOW ---
                        composable(
                            route = "dashboard_santri/{id}/{nama}/{email}/{role}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val role = backStackEntry.arguments?.getString("role") ?: "santri"

                            val isGuest = id == "guest_user"

                            SantriDashboardScreen(
                                userId = id,
                                namaUser = URLDecoder.decode(name, "UTF-8"),
                                navController = navController,
                                onNavigateToDashboard = { /* Stay */ },
                                onNavigateToJilid = {
                                    navController.navigate("santri_control_center/$id/$name/$email/$role")
                                },
                                onNavigateToProfile = {
                                    if (isGuest) navController.navigate("login") // Ke Login jika Guest
                                    else navController.navigate("profile/$id/$name/$email/$role")
                                },
                                onNavigateToBimbingan = {
                                    if (isGuest) {
                                        navController.navigate("login")
                                    } else {
                                        // Langsung ke detail bimbingan (Page yang menampilkan status dan guru)
                                        navController.navigate("santri_bimbingan_detail/$id/$name/$email")
                                    }
                                },
                                onNavigateToInfoProgram = {
                                    navController.navigate("info_program")
                                }
                            )
                        }

                        composable("santri_control_center/{id}/{nama}/{email}/{role}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val role = backStackEntry.arguments?.getString("role") ?: "santri"

                            SantriControlCenterScreen(
                                userId = id, namaUser = name, emailUser = email,
                                navController = navController,
                                onNavigateToMateri = { navController.navigate("menu_belajar/$id/$name/$email") },
                                onNavigateToRiwayat = { navController.navigate("riwayat_setoran/$id") },
                                onNavigateToProfile = { navController.navigate("profile/$id/$name/$email/$role") },
                                onNavigateToLatihan = {
                                    navController.navigate("latihan_makhraj") }
                            )
                        }

                        // --- UNIVERSAL PROFILE ---
                        composable(
                            route = "profile/{id}/{nama}/{email}/{role}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val n = backStackEntry.arguments?.getString("nama") ?: ""
                            val e = backStackEntry.arguments?.getString("email") ?: ""
                            val r = backStackEntry.arguments?.getString("role")?.lowercase() ?: "santri"

                            ProfileScreen(
                                userIdAsli = id, namaUser = URLDecoder.decode(n, "UTF-8"),
                                emailUser = URLDecoder.decode(e, "UTF-8"), role = r,
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToHome = {
                                    when (r) {
                                        "admin" -> navController.navigate("dashboard_admin/$id/$n/$e/$r") { popUpTo(0) }
                                        "guru" -> navController.navigate("dashboard_guru/$id/$n/$e/$r") { popUpTo(0) }
                                        else -> navController.navigate("dashboard_santri/$id/$n/$e/$r") { popUpTo(0) }
                                    }
                                },
                                onNavigateToJilid = {
                                    when (r) {
                                        "admin" -> navController.navigate("admin_control_center")
                                        "guru" -> navController.navigate("guru_control_center/$id")
                                        else -> navController.navigate("santri_control_center/$id/$n/$e/$r")
                                    }
                                }
                            )
                        }

                        // --- INFO PROGRAM & DETAIL ---
                        composable("info_program") {
                            ProgramListScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToDetail = { id -> navController.navigate("program_detail/$id") }
                            )
                        }

                        composable(
                            route = "program_detail/{programId}",
                            arguments = listOf(navArgument("programId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val progId = backStackEntry.arguments?.getString("programId") ?: "1"
                            val programData = listOf(
                                ProgramYatlunah(id = 1, nama = "Program Reguler", deskripsi = "Pembelajaran rutin...", targetPeserta = "Anak-anak & Dewasa", materiUtama = "Tahsin dasar", fiturUnggulan = listOf("Bimbingan 1-on-1"))
                            ).find { it.id.toString() == progId }

                            ProgramDetailScreen(
                                program = programData,
                                onBack = { navController.popBackStack() },
                                // ✅ FIXED: Parameter daftar_bimbingan disamakan agar tidak crash
                                onRegister = { navController.navigate("daftar_bimbingan/Guest/User/guest@mail.com") },
                                fiturUnggulan = listOf("✔ Bimbingan Intensif", "✔ Sertifikat", "✔ Akses Fleksibel")
                            )
                        }

                        // --- FITUR SANTRI LAINNYA ---
                        composable("santri_bimbingan_detail/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val viewModel: SantriViewModel = viewModel()
                            LaunchedEffect(id) { viewModel.fetchStatusBimbingan(id) }

                            SantriBimbinganDetailScreen(
                                status = viewModel.bimbinganStatus,
                                namaGuru = viewModel.namaGuru,
                                onBack = { navController.popBackStack() },
                                onNavigateToFormDaftar = { navController.navigate("daftar_bimbingan/$id/$name/$email") }
                            )
                        }

                        composable("menu_belajar/{id}/{nama}/{email}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            MenuBelajarScreen(
                                namaUser = URLDecoder.decode(name, "UTF-8"),
                                onBack = { navController.popBackStack() },
                                onNavigateToMateri = { navController.navigate("list_jilid/$id/$name/$email") },
                                onNavigateToRiwayat = { navController.navigate("riwayat_setoran/$id") }
                            )
                        }

                        composable("list_jilid/{id}/{nama}/{email}") { backStackEntry ->
                            val uid = backStackEntry.arguments?.getString("id") ?: ""
                            val isGuest = uid == "guest_user"

                            JilidListScreen(
                                onNavigateToDetail = { jid ->
                                    // Sesuai dokumen: Guest hanya boleh buka Jilid 1
                                    if (isGuest && jid > 1) {
                                        navController.navigate("register") // Atau tampilkan Dialog Register
                                    } else {
                                        navController.navigate("baca_jilid/$jid/$uid")
                                    }
                                },
                                onNavigateToHome = { navController.popBackStack() }
                            )
                        }

                        composable("baca_jilid/{jilidId}/{userId}") { backStackEntry ->
                            val jid = backStackEntry.arguments?.getString("jilidId")?.toInt() ?: 1
                            val uid = backStackEntry.arguments?.getString("userId") ?: ""

                            PdfJilidViewerScreen(
                                jilidId = jid,
                                userId = uid,
                                onNavigateToLatihan = { jilidLatihan, halamanLatihan ->
                                    // Mengarahkan ke rute latihan dan mengirimkan parameter jilid, halaman, dan userId
                                    navController.navigate("latihan_makhraj/$jilidLatihan/$halamanLatihan/$uid")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("riwayat_setoran/{userId}") { backStackEntry ->
                            val uid = backStackEntry.arguments?.getString("userId") ?: ""
                            RiwayatSetoranScreen(userId = uid, onBack = { navController.popBackStack() })
                        }

                        // --- ✅ DIKEMBALIKAN: RUTE ADMIN & FORM PENDAFTARAN YANG TERHAPUS ---
                        composable("user_management") {
                            UserManagementMenuScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToList = { r: String -> navController.navigate("user_list/$r") }
                            )
                        }


                        composable("user_list/{role}") { backStackEntry ->
                            val r = backStackEntry.arguments?.getString("role") ?: "santri"
                            UserListScreen(
                                role = r,
                                onBack = { navController.popBackStack() },
                                onNavigateToDetail = { id, nama, email ->
                                    // Gunakan elvis operator (?:) untuk menjamin parameter tidak null saat masuk ke route
                                    val finalId = id ?: "unknown"
                                    val finalNama = URLEncoder.encode(nama ?: "User", "UTF-8")
                                    val finalEmail = URLEncoder.encode(email ?: "", "UTF-8")

                                    navController.navigate("user_detail/$finalId/$finalNama/$finalEmail/$r")
                                }
                            )
                        }

                        composable(
                            route = "user_detail/{id}/{nama}/{email}/{role}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType }, // UUID ditangkap sebagai String
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val rawNama = backStackEntry.arguments?.getString("nama") ?: ""
                            val rawEmail = backStackEntry.arguments?.getString("email") ?: ""
                            val role = backStackEntry.arguments?.getString("role") ?: "santri"

                            // Decoding aman agar tidak crash jika ada karakter @ atau spasi
                            val decodedNama = try { URLDecoder.decode(rawNama, "UTF-8") } catch (e: Exception) { rawNama }
                            val decodedEmail = try { URLDecoder.decode(rawEmail, "UTF-8") } catch (e: Exception) { rawEmail }

                            UserDetailScreen(
                                userId = id,
                                userName = decodedNama,
                                userEmail = decodedEmail,
                                initialRole = role,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("admin_control_center") {
                            AdminControlCenterScreen(
                                onNavigateToUserMgmt = { navController.navigate("user_management") },
                                onNavigateToQuotes = { navController.navigate("admin_quotes") },
                                onNavigateToLaporan = { /* TODO */ },
                                // PERBAIKAN: Gunakan parameter tunggal onNavigateToQuestions
                                onNavigateToInputLatihan = { navController.navigate("question_monitoring") } ,
                                onBack = { navController.popBackStack() }
                            )
                        }

// 2. Tambahkan rute AdminQuestionScreen (Gabungan Input & Monitoring)

                        composable("input_latihan") {
                            val scope = rememberCoroutineScope()
                            val context = LocalContext.current

                            InputLatihanScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { soalBaru: LatihanSoal -> // ✅ Tipe data didefinisikan eksplisit
                                    scope.launch {
                                        try {
                                            // Memanggil API untuk menyimpan soal
                                            val response = RetrofitClient.latihanApi.tambahSoalLatihan(soalBaru)

                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Soal berhasil di-mapping!", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            } else {
                                                Toast.makeText(context, "Gagal menyimpan: ${response.code()}", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }

                        composable("admin_quotes") { AdminQuoteScreen(onBack = { navController.popBackStack() }) }

                        // Pastikan kata 'composable' tidak terhapus
                        composable("latihan_makhraj/{jilidId}/{halaman}/{userId}") { backStackEntry ->
                            val jidString = backStackEntry.arguments?.getString("jilidId")
                            val halString = backStackEntry.arguments?.getString("halaman")
                            val uid = backStackEntry.arguments?.getString("userId") ?: ""

                            val jid = jidString?.toIntOrNull() ?: 1
                            val hal = halString?.toIntOrNull() ?: 1

                            // Menggunakan import yang sudah bersih di atas
                            val latihanViewModel: LatihanViewModel = viewModel()

                            LaunchedEffect(jid, hal) {
                                latihanViewModel.fetchSoalSesi(jilid = jid, halaman = hal, uid = uid)
                            }

                            LatihanMakhrajScreen(
                                navController = navController,
                                viewModel = latihanViewModel
                            )
                        }

                        composable("question_monitoring") {
                            // Pastikan memanggil Screen Monitoring, BUKAN InputLatihanScreen
                            QuestionMonitoringScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
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
                            DaftarBimbinganScreen(
                                userId = id,
                                totalProgress = 85,
                                onBack = { navController.popBackStack() }
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
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("yatlunah_notif", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }
}