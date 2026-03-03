package com.yatlunah.app

import android.os.Bundle
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
// ✅ TAMBAHKAN IMPORT INI
import androidx.work.*
import com.yatlunah.app.data.worker.NotificationWorker
import java.util.concurrent.TimeUnit
// ----------------------
import com.yatlunah.app.ui.screen.login.LoginScreen
import com.yatlunah.app.ui.screen.register.RegisterScreen
import com.yatlunah.app.ui.screen.dashboard.DashboardScreen
import com.yatlunah.app.ui.screen.materi.PdfJilidViewerScreen
import com.yatlunah.app.ui.screen.materi.JilidListScreen
import com.yatlunah.app.ui.screen.splash.SplashScreen
import com.yatlunah.app.ui.screen.profile.ProfileScreen
import com.yatlunah.app.ui.theme.AplikasiYatlunahtestTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 1. SETUP WORKMANAGER (PENGINGAT BIMBINGAN)
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
                        // ... (Semua route composable kamu tetap sama seperti di atas)

                        // --- 1. SPLASH SCREEN ---
                        composable("splash") {
                            SplashScreen(
                                onTimeout = {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- 2. HALAMAN LOGIN ---
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { userId, namaUser, emailUser ->
                                    val encodedId = URLEncoder.encode(userId, StandardCharsets.UTF_8.toString())
                                    val encodedName = URLEncoder.encode(namaUser, StandardCharsets.UTF_8.toString())
                                    val encodedEmail = URLEncoder.encode(emailUser, StandardCharsets.UTF_8.toString())
                                    navController.navigate("dashboard_user/$encodedId/$encodedName/$encodedEmail") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- 3. HALAMAN REGISTER ---
                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }

                        // --- 4. DASHBOARD PESERTA (USER) ---
                        composable(
                            route = "dashboard_user/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val idUser = backStackEntry.arguments?.getString("id") ?: ""
                            val encodedName = backStackEntry.arguments?.getString("nama") ?: "User"
                            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
                            val namaFix = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())

                            DashboardScreen(
                                userId = idUser,
                                namaUser = namaFix,
                                onLogout = {
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                onNavigateToJilid = {
                                    navController.navigate("list_jilid/$idUser/$encodedName/$encodedEmail")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$idUser/$encodedName/$encodedEmail")
                                }
                            )
                        }

                        // --- 5. HALAMAN LIST JILID ---
                        composable(
                            route = "list_jilid/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val idUser = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("nama") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""

                            JilidListScreen(
                                onNavigateToDetail = { jilidId ->
                                    navController.navigate("baca_jilid/$jilidId/$idUser")
                                },
                                onNavigateToHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$idUser/$name/$email")
                                }
                            )
                        }

                        // --- 6. HALAMAN PROFILE ---
                        composable(
                            route = "profile/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val idUser = backStackEntry.arguments?.getString("id") ?: ""
                            val encodedName = backStackEntry.arguments?.getString("nama") ?: "User"
                            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
                            val namaFix = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())
                            val emailFix = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8.toString())

                            ProfileScreen(
                                userIdAsli = idUser,
                                namaUser = namaFix,
                                emailUser = emailFix,
                                onLogout = {
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                onNavigateToHome = { navController.popBackStack() },
                                onNavigateToJilid = {
                                    navController.navigate("list_jilid/$idUser/$encodedName/$encodedEmail")
                                }
                            )
                        }

                        // --- 7. HALAMAN BACA JILID ---
                        composable(
                            route = "baca_jilid/{jilidId}/{userId}",
                            arguments = listOf(
                                navArgument("jilidId") { type = NavType.IntType },
                                navArgument("userId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val jilidId = backStackEntry.arguments?.getInt("jilidId") ?: 1
                            val idUser = backStackEntry.arguments?.getString("userId") ?: ""
                            PdfJilidViewerScreen(
                                jilidId = jilidId,
                                pdfFileName = "pdf/jilid$jilidId.pdf",
                                userId = idUser,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    // ✅ FUNGSI UNTUK MENDAFTARKAN WORKER
    private fun setupDailyNotification() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS) // Muncul 10 detik setelah app dibuka
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "yatlunah_notif",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}