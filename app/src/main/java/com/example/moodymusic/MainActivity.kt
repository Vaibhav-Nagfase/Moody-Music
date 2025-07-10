package com.example.moodymusic

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodymusic.screen.LoginScreen
import com.example.moodymusic.screen.SignUpScreen
import com.example.moodymusic.ViewModel.AuthViewModel
import com.example.moodymusic.ViewModel.AuthViewModelFactory
import com.example.moodymusic.api.SpotifyTokenApi
import com.example.moodymusic.screen.MainView
import com.example.moodymusic.ui.theme.MoodyMusicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    private var accessToken: String? = null
    private lateinit var navController: NavHostController


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        setContent {
            navController = rememberNavController()
            val context = LocalContext.current
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))


            MoodyMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background)
                {
                    NavigationGraph(
                        authViewModel = authViewModel,
                        navController = navController,
                        onSpotifyConnect = { startSpotifyLogin() },
                        startDestination = if (isLoggedIn) Screen.MainView.route else Screen.LoginScreen.route
                    )

                }
            }
        }
    }

    // 🌟 Launch Spotify OAuth in Custom Tab
    fun startSpotifyLogin() {
        val CLIENT_ID = "ba29ceb9f05f497ea44dee28b5d5c573"
        val REDIRECT_URI = "moodymusic://callback"
        val scopes = "user-read-private user-read-email streaming"

        val url = "https://accounts.spotify.com/authorize" +
                "?client_id=$CLIENT_ID" +
                "&response_type=code" +
                "&redirect_uri=$REDIRECT_URI" +
                "&scope=${scopes.replace(" ", "%20")}"

        val customTabIntent = CustomTabsIntent.Builder().build()
        customTabIntent.launchUrl(this, Uri.parse(url))
    }

    // 🌟 Capture redirect and extract access token
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent?.data ?: return

        if (uri.toString().startsWith("moodymusic://callback")) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                Log.d("Spotify", "🎯 Auth Code: $code")
                exchangeCodeForToken(code)
            } else {
                Log.e("Spotify", "❌ Authorization code missing")
            }
        }
    }

    fun exchangeCodeForToken(code: String) {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://spotify-backend-e92q.onrender.com/") // 🔁 Replace with actual IP or deployed backend
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SpotifyTokenApi::class.java)

        val requestBody = mapOf("code" to code)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getToken(requestBody)
                val accessToken = response.access_token

                Log.d("Spotify", "✅ Access Token: $accessToken")

                getSharedPreferences("prefs", MODE_PRIVATE).edit()
                    .putString("spotify_token", accessToken)
                    .apply()

                runOnUiThread {
                    navController.navigate(Screen.MainView.route)
                }

            } catch (e: Exception) {
                Log.e("Spotify", "❌ Token Exchange Failed", e)
            }
        }
    }




}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    onSpotifyConnect: () -> Unit,
    startDestination: String
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignUpScreen.route){
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {navController.navigate(Screen.LoginScreen.route)}
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {navController.navigate(Screen.SignUpScreen.route)}
            ){
                navController.navigate(Screen.MainView.route)
            }
        }

        composable(Screen.MainView.route){
            MainView(onSpotifyConnect = onSpotifyConnect, authViewModel = authViewModel){
                navController.navigate(Screen.LoginScreen.route){
                    popUpTo(Screen.MainView.route) { inclusive = true }
                }
            }
        }


    }
}


