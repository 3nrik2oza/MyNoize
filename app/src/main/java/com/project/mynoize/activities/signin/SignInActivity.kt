package com.project.mynoize.activities.signin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.mynoize.activities.signin.ui.theme.MyNoizeTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.project.mynoize.activities.main.MainActivity
import com.project.mynoize.managers.GoogleAuthUiClient
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class SignInActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyNoizeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = SplashScreen
                ){
                    composable<SplashScreen> {
                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {
                                val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                navController.navigate(ScreenSignIn)
                            }
                        }

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            CircularProgressIndicator(
                                modifier = Modifier.width(64.dp)
                            )
                        }

                    }

                    composable<ScreenSignIn>{
                        val viewModel = koinViewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null){
                                val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if(result.resultCode == RESULT_OK){
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if(state.isSignInSuccessful){
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }


                        SignInScreen(
                            navController,
                            onSignInWithGoogleClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    if (signInIntentSender == null) {
                                        Toast.makeText(applicationContext, "Failed to get sign-in intent", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender
                                        ).build()
                                    )
                                }
                            },
                            onSuccessfulSignInWithEmail = {
                                val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            alertDialogState = viewModel.alertDialogState.collectAsStateWithLifecycle().value,
                            state = viewModel.state.collectAsStateWithLifecycle().value,
                            onEvent = {
                                viewModel.onEvent(it)
                            }
                        )
                    }
                    composable<ScreenSignUp>{
                        SignUpScreen(navController)
                    }
                }
            }
        }
    }
}


@Serializable
object SplashScreen

@Serializable
object ScreenSignIn

@Serializable
object ScreenSignUp
