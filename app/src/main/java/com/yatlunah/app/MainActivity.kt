package com.yatlunah.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.yatlunah.app.data.worker.NotificationWorker
import java.util.concurrent.TimeUnit

// --- IMPORT MODEL ---
import com.yatlunah.app.data.model.Setoran
import com.yatlunah.app.ui.screen.admin.AdminDashboardScreen

// --- IMPORT SCREEN UMUM ---
import com.yatlunah.app.ui.screen.login.LoginScreen
import com.yatlunah.app.ui.screen.register.RegisterScreen
import com.yatlunah.app.ui.screen.dashboard.DashboardScreen
import com.yatlunah.app.ui.screen.materi.*
import com.yatlunah.app.ui.screen.splash.SplashScreen
import com.yatlunah.app.ui.screen.profile.ProfileScreen

// --- IMPORT SCREEN GURU & ADMIN ---
import com.yatlunah.app.ui.screen.guru.GuruDashboardScreen
import com.yatlunah.app.ui.screen.guru.GuruPenilaianDetailScreen
import com.yatlunah.app.ui.screen.guru.GuruSetoranQueueScreen
import com.yatlunah.app.ui.screen.guru.GuruJilidMenuScreen
import com.yatlunah.app.ui.screen.admin.UserManagementMenuScreen
import com.yatlunah.app.ui.screen.admin.UserListScreen
import com.yatlunah.app.ui.screen.admin.UserDetailScreen

import com.yatlunah.app.ui.theme.AplikasiYatlunahtestTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// ... import lainnya tetap sama ...

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
                        // (Tetap sama seperti kode Anda)
                        composable("splash") {
                            SplashScreen(onTimeout = {
                                navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                            })
                        }

                        // Di MainActivity.kt bagian LoginScreen
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { userId, namaUser, emailUser, role ->
                                    val encodedId = URLEncoder.encode(userId, "UTF-8")
                                    val encodedName = URLEncoder.encode(namaUser, "UTF-8")

                                    // ✅ BERSIHKAN STRING ROLE
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
                                        else -> { // Santri / Peserta
                                            val encodedEmail = URLEncoder.encode(emailUser, "UTF-8")
                                            navController.navigate("dashboard_user/$encodedId/$encodedName/$encodedEmail") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // --- 2. DASHBOARD GURU ---
                        composable("dashboard_guru/{id}/{nama}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("id") ?: ""
                            val encodedName = backStackEntry.arguments?.getString("nama") ?: ""
                            val nameGuru = URLDecoder.decode(encodedName, "UTF-8")

                            GuruDashboardScreen(
                                namaGuru = nameGuru,
                                onNavigateToAntrean = {
                                    // Kirim ID Guru agar bisa digunakan saat penilaian nanti
                                    navController.navigate("guru_menu_jilid/$idGuru")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$idGuru/$encodedName/guru@yatlunah.com")
                                }
                            )
                        }

                        composable(
                            route = "dashboard_admin/{id}/{nama}", // ✅ Pastikan ada 2 parameter
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
                                onNavigateToUserMgmt = { navController.navigate("user_management") },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$id/$rawName/admin@yatlunah.com")
                                }
                            )
                        }

                        // --- 3. GURU: MENU PILIH JILID ---
                        composable("guru_menu_jilid/{idGuru}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""
                            GuruJilidMenuScreen(
                                onNavigateToHome = { navController.popBackStack() },
                                onNavigateToProfile = { navController.navigate("login") },
                                onNavigateToQueue = { jilidId ->
                                    navController.navigate("guru_antrean/$jilidId/$idGuru")
                                }
                            )
                        }

                        // --- 4. GURU: DAFTAR ANTREAN ---
                        composable(
                            route = "guru_antrean/{jilidId}/{idGuru}",
                            arguments = listOf(
                                navArgument("jilidId") { type = NavType.IntType },
                                navArgument("idGuru") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val jilidId = backStackEntry.arguments?.getInt("jilidId") ?: 1
                            val idGuru = backStackEntry.arguments?.getString("idGuru") ?: ""

                            GuruSetoranQueueScreen(
                                jilidTarget = jilidId,
                                onBack = { navController.popBackStack() },
                                onNavigateToPenilaian = { setoran: Setoran ->
                                    val encName = URLEncoder.encode(setoran.namaSantri ?: "Siswa", "UTF-8")
                                    val encAudio = URLEncoder.encode(setoran.audioUrl ?: "", "UTF-8")

                                    // Tambahkan setoran.id dan idGuru ke rute
                                    navController.navigate("guru_nilai/${setoran.id}/$idGuru/$encName/${setoran.jilid}/${setoran.halaman}/$encAudio")
                                }
                            )
                        }

                        // --- 5. GURU: DETAIL PENILAIAN (UPDATE PARAMETER) ---
                        composable(
                            route = "guru_nilai/{setoranId}/{idGuru}/{nama}/{jilid}/{halaman}/{audioUrl}",
                            arguments = listOf(
                                navArgument("setoranId") { type = NavType.IntType },
                                navArgument("idGuru") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("jilid") { type = NavType.IntType },
                                navArgument("halaman") { type = NavType.IntType },
                                navArgument("audioUrl") { type = NavType.StringType; nullable = true; defaultValue = "" }
                            )
                        ) { backStackEntry ->
                            val sId = backStackEntry.arguments?.getInt("setoranId") ?: 0
                            val gId = backStackEntry.arguments?.getString("idGuru") ?: ""
                            val nama = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val jilid = backStackEntry.arguments?.getInt("jilid") ?: 1
                            val halaman = backStackEntry.arguments?.getInt("halaman") ?: 1
                            val rawAudio = backStackEntry.arguments?.getString("audioUrl") ?: ""
                            val audioUrl = URLDecoder.decode(rawAudio, "UTF-8")

                            GuruPenilaianDetailScreen(
                                setoranId = sId,
                                idGuru = gId,
                                nama = nama,
                                jilid = jilid,
                                halaman = halaman,
                                audioUrl = audioUrl,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- 6. USER SECTION (SANTRI) ---
                        composable("dashboard_user/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            DashboardScreen(
                                userId = id, namaUser = URLDecoder.decode(name, "UTF-8"),
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToJilid = { navController.navigate("menu_belajar/$id/$name/$email") },
                                onNavigateToProfile = { navController.navigate("profile/$id/$name/$email") }
                            )
                        }

                        composable("menu_belajar/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            MenuBelajarScreen(
                                namaUser = URLDecoder.decode(name, "UTF-8"),
                                onNavigateToHome = { navController.navigate("dashboard_user/$id/$name/$email") { popUpTo(0) } },
                                onNavigateToProfile = { navController.navigate("profile/$id/$name/$email") },
                                onNavigateToMateri = { navController.navigate("list_jilid/$id/$name/$email") },
                                onNavigateToRiwayat = { navController.navigate("riwayat_setoran/$id") }
                            )
                        }

                        // --- 7. REGISTER, PROFILE, JILID, RIWAYAT ---
                        composable("register") { RegisterScreen(onNavigateToLogin = { navController.popBackStack() }) }

                        composable("profile/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val n = backStackEntry.arguments?.getString("nama") ?: ""
                            val e = backStackEntry.arguments?.getString("email") ?: ""
                            ProfileScreen(
                                userIdAsli = id, namaUser = URLDecoder.decode(n, "UTF-8"), emailUser = URLDecoder.decode(e, "UTF-8"),
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToHome = { navController.popBackStack() },
                                onNavigateToJilid = { navController.navigate("menu_belajar/$id/$n/$e") }
                            )
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

                        composable(
                            route = "baca_jilid/{jilidId}/{userId}",
                            arguments = listOf(
                                navArgument("jilidId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            // Ambil jilidId, jika null atau 0, paksa ke 1 agar tidak error "URL Kosong"
                            val jidParam = backStackEntry.arguments?.getInt("jilidId") ?: 1
                            val jid = if (jidParam <= 0) 1 else jidParam // <-- Tambahkan logika ini

                            val uid = backStackEntry.arguments?.getString("userId") ?: ""

                            PdfJilidViewerScreen(
                                jilidId = jid,
                                userId = uid,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- 8. ADMIN SECTION ---
                        composable("user_management") {
                            UserManagementMenuScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToList = { r: String -> navController.navigate("user_list/$r") }
                            )
                        }

                        composable("user_list/{role}") { backStackEntry ->
                            val r = backStackEntry.arguments?.getString("role") ?: "peserta"
                            UserListScreen(
                                role = r, onBack = { navController.popBackStack() },
                                onNavigateToDetail = { id: String, n: String, e: String ->
                                    val en = URLEncoder.encode(n, "UTF-8")
                                    val ee = URLEncoder.encode(e, "UTF-8")
                                    navController.navigate("user_detail/$id/$en/$ee")
                                }
                            )
                        }

                        composable("user_detail/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val n = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val e = URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8")
                            UserDetailScreen(userId = id, userName = n, userEmail = e, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }

    private fun setupDailyNotification() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS).build()
        WorkManager.getInstance(this).enqueueUniqueWork("yatlunah_notif", ExistingWorkPolicy.REPLACE, workRequest)
    }
}