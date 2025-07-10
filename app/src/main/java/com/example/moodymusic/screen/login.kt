package com.example.moodymusic.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusic.R
import com.example.moodymusic.ViewModel.AuthViewModel
import com.example.moodymusic.data.Result
import com.example.moodymusic.util.pingBackend
import kotlinx.coroutines.delay


@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.log_back3),
        contentDescription = "Background Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginTriggered by remember { mutableStateOf(false) }

    val result by authViewModel.authResult.observeAsState()
    val context = LocalContext.current

    // 🔄 Trigger this only when we know login was initiated
    LaunchedEffect(result, loginTriggered) {
        if (loginTriggered) {
            when (result) {
                is Result.Success -> {
                    isLoading = true
                    pingBackend()
                    delay(25000) // 🌐 Wait for Render to wake
                    isLoading = false
                    onSignInSuccess()
                    loginTriggered = false // reset
                }

                is Result.Error -> {
                    Toast.makeText(context, "Invalid Email/Password", Toast.LENGTH_LONG).show()
                    loginTriggered = false // reset
                }

                else -> {}
            }
        }
    }

    // Loading Screen
    if (isLoading) {
        LoadingScreen("Waking up server...")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(130.dp))

        Text(
            text = "Login",
            color = Color.Cyan,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(70.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                authViewModel.login(email, password)
                loginTriggered = true // 🔁 Now track if this login was user-triggered
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Don't have an account? Sign up.",
            modifier = Modifier.clickable { onNavigateToSignUp() }
        )
    }
}



//@Preview(showBackground = true)
//@Composable
//fun LoginPreview() {
////    LoginScreen()
//}