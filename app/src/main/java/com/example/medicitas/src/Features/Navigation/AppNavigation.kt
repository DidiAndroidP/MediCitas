package com.example.medicitas.src.Features.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicitas.src.Features.Login.presentation.view.LoginScreen
import com.example.medicitas.src.Features.Register.presentation.view.RegisterScreen
import com.example.medicitas.src.Features.Dashboard.presentation.view.DashboardScreen
import com.example.medicitas.src.Features.Profile.presentation.view.ProfileScreen
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentScreen
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentDetailScreen
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentCreateScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token: String ->
                    navController.navigate("dashboard/$token") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            DashboardScreen(
                token = token,
                onNavigateToProfile = {
                    navController.navigate("profile/$token")
                },
                onNavigateToAppointments = {
                    navController.navigate("appointments/$token")
                },
                onNavigateToCreateAppointment = {
                    navController.navigate("create_appointment/$token")
                }
            )
        }

        composable("profile/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ProfileScreen(
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("appointments/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            AppointmentScreen(
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onViewDetails = { appointmentId: Int ->
                    navController.navigate("appointment_detail/$appointmentId/$token")
                }
            )
        }

        composable("appointment_detail/{appointmentId}/{token}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toIntOrNull() ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""

            AppointmentDetailScreen(
                appointmentId = appointmentId,
                token = token,
                onClose = {
                    navController.popBackStack()
                }
            ) {
            }
        }

        composable("create_appointment/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            AppointmentCreateScreen(
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.popBackStack()
                }
            )
        }
    }
}