package com.yatlunah.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

// --- IMPORT SCREEN UMUM ---
import com.yatlunah.app.ui.screen.login.LoginScreen
import com.yatlunah.app.ui.screen.register.RegisterScreen
import com.yatlunah.app.ui.screen.dashboard.DashboardScreen
import com.yatlunah.app.ui.screen.materi.*
import com.yatlunah.app.ui.screen.splash.SplashScreen
import com.yatlunah.app.ui.screen.profile.ProfileScreen

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

import androidx.compose.ui.tooling.preview.Preview
import com.yatlunah.app.data.model.ProgramYatlunah
import com.yatlunah.app.ui.screen.bimbingan.DaftarBimbinganScreen
import com.yatlunah.app.ui.screen.info_program.ProgramDetailScreen
import com.yatlunah.app.ui.screen.info_program.ProgramListScreen

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

                        // --- 2. DASHBOARD GURU ---
                        composable("dashboard_guru/{id}/{nama}") { backStackEntry ->
                            val idGuru = backStackEntry.arguments?.getString("id") ?: ""
                            val encodedName = backStackEntry.arguments?.getString("nama") ?: ""
                            val nameGuru = URLDecoder.decode(encodedName, "UTF-8")

                            GuruDashboardScreen(
                                namaGuru = nameGuru,
                                onNavigateToAntrean = {
                                    navController.navigate("guru_menu_jilid/$idGuru")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$idGuru/$encodedName/guru@yatlunah.com")
                                }
                            )
                        }

                        // --- ADMIN FLOW ---
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
                                        else -> { // Peserta / User
                                            val encodedEmail = URLEncoder.encode(emailUser, "UTF-8")
                                            navController.navigate("dashboard_user/$encodedId/$encodedName/$encodedEmail") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // --- 3. GURU FLOW ---
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

                        // --- 4. USER FLOW ---
                        composable(
                            route = "dashboard_user/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""

                            DashboardScreen(
                                userId = id,
                                namaUser = URLDecoder.decode(name, "UTF-8"),
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToDashboard = {
                                    if (navController.currentBackStackEntry?.destination?.route != "dashboard_user/{id}/{nama}/{email}") {
                                        navController.navigate("dashboard_user/$id/$name/$email") {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                onNavigateToJilid = {
                                    navController.navigate("menu_belajar/$id/$name/$email") {
                                        launchSingleTop = true
                                        popUpTo("dashboard_user/{id}/{nama}/{email}") {
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
                                    navController.navigate("daftar_bimbingan/$id/85")
                                },
                                onNavigateToInfoProgram = {
                                    navController.navigate("info_program")
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
                                onNavigateToHome = { navController.popBackStack() },
                                onNavigateToProfile = { navController.navigate("profile/$id/$name/$email") },
                                onNavigateToMateri = { navController.navigate("list_jilid/$id/$name/$email") },
                                onNavigateToRiwayat = { navController.navigate("riwayat_setoran/$id") }
                            )
                        }

                        composable("profile/{id}/{nama}/{email}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val n = backStackEntry.arguments?.getString("nama") ?: ""
                            val e = backStackEntry.arguments?.getString("email") ?: ""
                            val decodedEmail = URLDecoder.decode(e, "UTF-8")

                            ProfileScreen(
                                userIdAsli = id,
                                namaUser = URLDecoder.decode(n, "UTF-8"),
                                emailUser = decodedEmail,
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToHome = { navController.popBackStack() },
                                onNavigateToJilid = {
                                    if (decodedEmail.contains("admin")) {
                                        navController.navigate("admin_control_center")
                                    } else {
                                        navController.navigate("menu_belajar/$id/$n/$e")
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
                            val r = backStackEntry.arguments?.getString("role") ?: "peserta"
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
                            val r = backStackEntry.arguments?.getString("role") ?: "peserta"

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

                        composable("daftar_bimbingan/{userId}/{progress}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            val progress = backStackEntry.arguments?.getString("progress")?.toInt() ?: 0

                            DaftarBimbinganScreen(
                                userId = userId,
                                totalProgress = progress,
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
                            // 1. SOLUSI "Elvis operator" & "No cast needed":
                            // Gunakan .orEmpty().ifEmpty {} agar Kotlin tidak protes soal null atau cast (as String?)
                            val idString = backStackEntry.arguments?.getString("programId").orEmpty().ifEmpty { "1" }

                            val daftarProgram = listOf(
                                ProgramYatlunah(
                                    id = 1,
                                    nama = "Program Reguler",
                                    deskripsi = "Pembelajaran rutin Jilid 1-6...",
                                    targetPeserta = "Anak-anak & Dewasa",
                                    materiUtama = "Tahsin dasar", // <-- Tambahkan koma di sini
                                    fiturUnggulan = listOf("Bimbingan 1-on-1", "Sertifikat") // <-- Masukkan data yang kurang
                                )
                            )

                            val programData = daftarProgram.find { it.id.toString() == idString }

                            // 2. SOLUSI "No value passed for fiturUnggulan":
                            ProgramDetailScreen(
                                program = programData,
                                onBack = { navController.popBackStack() },
                                onRegister = { _ ->
                                    navController.navigate("daftar_bimbingan/user_default/85")
                                },
                                // WAJIB DITULIS: Karena di ProgramDetailScreen parameter ini tidak punya nilai default,
                                // kita harus mengirimkan nilainya dari sini.
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

// --- KUMPULAN PREVIEW UNTUK SEMUA SCREEN ---
@Preview(showBackground = true, name = "1. Splash Screen")
@Composable
fun PreviewSplash() {
    AplikasiYatlunahtestTheme {
        SplashScreen(onTimeout = {})
    }
}

@Preview(showBackground = true, name = "2. Login Screen")
@Composable
fun PreviewLogin() {
    AplikasiYatlunahtestTheme {
        LoginScreen(onNavigateToRegister = {}, onLoginSuccess = { _, _, _, _ -> })
    }
}

@Preview(showBackground = true, name = "3. Register Screen")
@Composable
fun PreviewRegister() {
    AplikasiYatlunahtestTheme {
        RegisterScreen(onNavigateToLogin = {})
    }
}

@Preview(showBackground = true, name = "4. Dashboard Peserta")
@Composable
fun PreviewDashboardUser() {
    AplikasiYatlunahtestTheme {
        DashboardScreen(
            userId = "u1", namaUser = "Ahmad Yusuf",
            onLogout = {}, onNavigateToJilid = {}, onNavigateToProfile = {},
            onNavigateToDashboard = {}, onNavigateToBimbingan = {}, onNavigateToInfoProgram = {}
        )
    }
}

@Preview(showBackground = true, name = "5. Menu Belajar")
@Composable
fun PreviewMenuBelajar() {
    AplikasiYatlunahtestTheme {
        MenuBelajarScreen(
            namaUser = "Ahmad Yusuf", onNavigateToHome = {},
            onNavigateToProfile = {}, onNavigateToMateri = {}, onNavigateToRiwayat = {}
        )
    }
}

@Preview(showBackground = true, name = "6. List Jilid")
@Composable
fun PreviewJilidList() {
    AplikasiYatlunahtestTheme {
        JilidListScreen(onNavigateToDetail = {}, onNavigateToHome = {})
    }
}

@Preview(showBackground = true, name = "7. Profile Screen")
@Composable
fun PreviewProfile() {
    AplikasiYatlunahtestTheme {
        ProfileScreen(
            userIdAsli = "u1", namaUser = "Ahmad Yusuf", emailUser = "ahmad@yatlunah.com",
            onLogout = {}, onNavigateToHome = {}, onNavigateToJilid = {}
        )
    }
}

@Preview(showBackground = true, name = "8. Dashboard Guru")
@Composable
fun PreviewGuruDashboard() {
    AplikasiYatlunahtestTheme {
        GuruDashboardScreen(namaGuru = "Ustadz Mansur", onNavigateToAntrean = {}, onNavigateToProfile = {})
    }
}

@Preview(showBackground = true, name = "9. Menu Jilid Guru")
@Composable
fun PreviewGuruJilidMenu() {
    AplikasiYatlunahtestTheme {
        GuruJilidMenuScreen(onNavigateToHome = {}, onNavigateToProfile = {}, onNavigateToQueue = {})
    }
}

@Preview(showBackground = true, name = "10. Dashboard Admin")
@Composable
fun PreviewAdminDashboard() {
    AplikasiYatlunahtestTheme {
        AdminDashboardScreen(namaAdmin = "Super Admin", onNavigateToUserMgmt = {}, onNavigateToProfile = {})
    }
}

@Preview(showBackground = true, name = "11. Admin Control Center")
@Composable
fun PreviewAdminControl() {
    AplikasiYatlunahtestTheme {
        AdminControlCenterScreen(onNavigateToUserMgmt = {}, onNavigateToQuotes = {}, onNavigateToLaporan = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "12. User Management Menu")
@Composable
fun PreviewUserMgmtMenu() {
    AplikasiYatlunahtestTheme {
        UserManagementMenuScreen(onBack = {}, onNavigateToList = {})
    }
}