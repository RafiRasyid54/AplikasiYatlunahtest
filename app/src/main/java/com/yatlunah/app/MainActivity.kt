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
                                navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                            })
                        }

                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { userId, namaUser, emailUser, role ->
                                    val encodedId = URLEncoder.encode(userId, "UTF-8")
                                    val encodedName = URLEncoder.encode(namaUser, "UTF-8")
                                    when (role.lowercase()) {
                                        "admin", "guru" -> {
                                            navController.navigate("dashboard_guru/$encodedId/$encodedName") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                        else -> {
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
                                onNavigateToAntrean = { navController.navigate("guru_menu_jilid") },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$idGuru/$encodedName/guru@yatlunah.com")
                                }
                            )
                        }

                        // --- 3. GURU: MENU PILIH JILID (DENGAN NAVBAR) ---
                        composable("guru_menu_jilid") {
                            // Mengambil data dari antrean sebelumnya jika diperlukan,
                            // namun di sini kita fokus pada parameter yang sesuai dengan GuruJilidMenuScreen terbaru.
                            GuruJilidMenuScreen(
                                onNavigateToHome = {
                                    navController.popBackStack()
                                },
                                onNavigateToProfile = {
                                    // Sesuaikan dengan kebutuhan navigasi profil guru
                                    navController.navigate("login") // Contoh sementara
                                },
                                onNavigateToQueue = { jilidId ->
                                    navController.navigate("guru_antrean/$jilidId")
                                }
                            )
                        }

                        // --- 4. GURU: DAFTAR ANTREAN PER JILID ---
                        composable(
                            route = "guru_antrean/{jilidId}",
                            arguments = listOf(navArgument("jilidId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val jilidId = backStackEntry.arguments?.getInt("jilidId") ?: 1
                            GuruSetoranQueueScreen(
                                jilidTarget = jilidId,
                                onBack = { navController.popBackStack() },
                                onNavigateToPenilaian = { setoran: Setoran ->
                                    // ✅ ENCODE Nama dan URL Audio agar tidak error saat navigasi
                                    val encName = URLEncoder.encode(setoran.namaSantri ?: "Siswa", "UTF-8")
                                    val encAudio = URLEncoder.encode(setoran.audioUrl ?: "", "UTF-8")

                                    // ✅ Tambahkan {audioUrl} ke dalam rute navigasi
                                    navController.navigate("guru_nilai/$encName/${setoran.jilid}/${setoran.halaman}/$encAudio")
                                }
                            )
                        }

                        // --- 5. GURU: DETAIL PENILAIAN ---
                        composable(
                            route = "guru_nilai/{nama}/{jilid}/{halaman}/{audioUrl}",
                            arguments = listOf(
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("jilid") { type = NavType.IntType },
                                navArgument("halaman") { type = NavType.IntType },
                                navArgument("audioUrl") {
                                    type = NavType.StringType
                                    nullable = true // ✅ Berikan izin jika null agar tidak crash
                                    defaultValue = "" // ✅ Berikan default kosong agar log "URL Kosong" terdeteksi
                                }
                            )
                        ) { backStackEntry ->
                            val nama = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val jilid = backStackEntry.arguments?.getInt("jilid") ?: 1
                            val halaman = backStackEntry.arguments?.getInt("halaman") ?: 1

                            // Ambil raw audio, pastikan tidak null
                            val rawAudio = backStackEntry.arguments?.getString("audioUrl") ?: ""

                            // DEBUG: Lihat di logcat apakah navigasi mengirim string yang benar
                            android.util.Log.d("RAFI_NAV", "Raw Audio URL dari Nav: $rawAudio")

                            // Lakukan decoding
                            val audioUrl = try {
                                URLDecoder.decode(rawAudio, "UTF-8")
                            } catch (e: Exception) {
                                ""
                            }

                            GuruPenilaianDetailScreen(
                                nama = nama,
                                jilid = jilid,
                                halaman = halaman,
                                audioUrl = audioUrl,
                                onBack = { navController.popBackStack() },
                                onConfirm = { nilai, catatan ->
                                    Toast.makeText(this@MainActivity, "Berhasil menilai $nama", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
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