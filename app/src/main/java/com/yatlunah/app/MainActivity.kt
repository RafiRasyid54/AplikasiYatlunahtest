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
import androidx.work.*
import com.yatlunah.app.data.worker.NotificationWorker
import java.util.concurrent.TimeUnit
import com.yatlunah.app.ui.screen.login.LoginScreen
import com.yatlunah.app.ui.screen.register.RegisterScreen
import com.yatlunah.app.ui.screen.dashboard.DashboardScreen
import com.yatlunah.app.ui.screen.materi.PdfJilidViewerScreen
import com.yatlunah.app.ui.screen.materi.JilidListScreen
import com.yatlunah.app.ui.screen.splash.SplashScreen
import com.yatlunah.app.ui.screen.profile.ProfileScreen
// IMPORT SCREEN ADMIN
import com.yatlunah.app.ui.screen.admin.AdminDashboardScreen
import com.yatlunah.app.ui.screen.admin.UserListScreen
import com.yatlunah.app.ui.screen.admin.UserManagementMenuScreen
import com.yatlunah.app.ui.screen.admin.UserDetailScreen // ✅ Tambahkan ini
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
                                onLoginSuccess = { userId, namaUser, emailUser, role ->
                                    val encodedId = URLEncoder.encode(userId, StandardCharsets.UTF_8.toString())
                                    val encodedName = URLEncoder.encode(namaUser, StandardCharsets.UTF_8.toString())

                                    when (role.lowercase()) {
                                        "admin" -> {
                                            navController.navigate("dashboard_admin/$encodedId/$encodedName") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                        else -> {
                                            val encodedEmail = URLEncoder.encode(emailUser, StandardCharsets.UTF_8.toString())
                                            navController.navigate("dashboard_user/$encodedId/$encodedName/$encodedEmail") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // --- 3. DASHBOARD ADMIN ---
                        composable(
                            route = "dashboard_admin/{id}/{nama}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val idAdmin = backStackEntry.arguments?.getString("id") ?: ""
                            val nameAdmin = backStackEntry.arguments?.getString("nama") ?: "Admin"
                            val emailAdmin = "admin@yatlunah.com"

                            AdminDashboardScreen(
                                onNavigateToUserMgmt = { navController.navigate("user_management") },
                                onNavigateToHome = { /* Sudah di Home */ },
                                onNavigateToProfile = {
                                    val encodedName = URLEncoder.encode(nameAdmin, StandardCharsets.UTF_8.toString())
                                    val encodedEmail = URLEncoder.encode(emailAdmin, StandardCharsets.UTF_8.toString())
                                    // Admin melihat profilnya sendiri (boleh ganti pass/logout)
                                    navController.navigate("profile/$idAdmin/$encodedName/$encodedEmail")
                                }
                            )
                        }

                        // --- 4. MANAJEMEN USER MENU ---
                        composable("user_management") {
                            UserManagementMenuScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToList = { role ->
                                    navController.navigate("user_list/$role")
                                }
                            )
                        }

                        // --- 5. LIST USER (Berdasarkan Role) ---
                        composable(
                            route = "user_list/{role}",
                            arguments = listOf(navArgument("role") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role") ?: "peserta"
                            UserListScreen(
                                role = role,
                                onBack = { navController.popBackStack() },
                                onNavigateToDetail = { id, nama, email ->
                                    val encName = URLEncoder.encode(nama, StandardCharsets.UTF_8.toString())
                                    val encEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                                    // ✅ ARAHKAN KE DETAIL KHUSUS (Bukan Profile biasa)
                                    navController.navigate("user_detail/$id/$encName/$encEmail")
                                }
                            )
                        }

                        // --- 6. DETAIL USER KHUSUS UNTUK ADMIN ---
                        composable(
                            route = "user_detail/{id}/{nama}/{email}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("nama") { type = NavType.StringType },
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val nama = URLDecoder.decode(backStackEntry.arguments?.getString("nama") ?: "", "UTF-8")
                            val email = URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8")

                            UserDetailScreen(
                                userId = id,
                                userName = nama,
                                userEmail = email,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- 7. REGISTER ---
                        composable("register") {
                            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
                        }

                        // --- 8. DASHBOARD PESERTA (MURID) ---
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
                            val namaFix = URLDecoder.decode(encodedName, "UTF-8")

                            DashboardScreen(
                                userId = idUser,
                                namaUser = namaFix,
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToJilid = { navController.navigate("list_jilid/$idUser/$encodedName/$encodedEmail") },
                                onNavigateToProfile = { navController.navigate("profile/$idUser/$encodedName/$encodedEmail") }
                            )
                        }

                        // --- 9. PROFILE SCREEN (Bisa diakses Admin & User) ---
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
                            ProfileScreen(
                                userIdAsli = idUser,
                                namaUser = URLDecoder.decode(encodedName, "UTF-8"),
                                emailUser = URLDecoder.decode(encodedEmail, "UTF-8"),
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToHome = { navController.popBackStack() },
                                onNavigateToJilid = { navController.navigate("list_jilid/$idUser/$encodedName/$encodedEmail") }
                            )
                        }

                        // --- 10. MATERI & PDF VIEWER ---
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
                                onNavigateToDetail = { jilidId -> navController.navigate("baca_jilid/$jilidId/$idUser") },
                                onNavigateToHome = { navController.popBackStack() },
                                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                                onNavigateToProfile = { navController.navigate("profile/$idUser/$name/$email") }
                            )
                        }

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

    private fun setupDailyNotification() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork("yatlunah_notif", ExistingWorkPolicy.REPLACE, workRequest)
    }
}