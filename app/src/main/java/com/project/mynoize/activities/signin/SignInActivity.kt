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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.mynoize.activities.signin.ui.theme.MyNoizeTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.project.mynoize.activities.main.MainActivity
import com.project.mynoize.activities.signin.event.SignInEvent
import com.project.mynoize.activities.signin.event.SignUpEvent
import com.project.mynoize.managers.GoogleAuthUiClient
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform.getKoin

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

                /*
                LaunchedEffect(true) {
                    val koin = getKoin()
                    if (koin.getScopeOrNull("USER_SESSION") == null) {
                        koin.createScope(
                            "USER_SESSION",
                            named("USER_SCOPE")
                        )
                    }
                }*/

                NavHost(
                    navController = navController,
                    startDestination = SplashScreen
                ){
                    composable<SplashScreen> {
                        LaunchedEffect(key1 = Unit) {

                            if(googleAuthUiClient.getSignedInUser() != null) {
                                createUserScopeIfNeeded()
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

                    composable<ScreenSignIn>(
                        exitTransition = { slideOutHorizontally{ initialOffset ->
                            initialOffset
                        } },
                        popEnterTransition = { slideInHorizontally() }
                    ){
                        val viewModel = koinViewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null){
                                createUserScopeIfNeeded()
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
                                createUserScopeIfNeeded()
                                val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        LaunchedEffect(key1 = LocalContext.current) {
                            viewModel.validSignInEvent.collect {
                                if(it){
                                    createUserScopeIfNeeded()
                                    val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }


                        SignInScreen(
                            navController,
                            alertDialogState = viewModel.alertDialogState.collectAsStateWithLifecycle().value,
                            state = viewModel.state.collectAsStateWithLifecycle().value,
                            onEvent = { event->
                                when(event){
                                    is SignInEvent.OnSignInWithGoogleClick -> {
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
                                    }
                                    is SignInEvent.OnSuccessFulSignInWithEmail ->{
                                        createUserScopeIfNeeded()
                                        val intent = Intent(applicationContext.applicationContext, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            }
                        )
                    }
                    composable<ScreenSignUp>(
                        enterTransition = {slideInHorizontally{ initialOffset ->
                            initialOffset
                        }},
                        exitTransition = { slideOutHorizontally{ initialOffset ->
                            initialOffset
                        }}
                    ){
                        val viewModel = koinViewModel<SignUpViewModel>()
                        SignUpScreen(
                            alertDialogState = viewModel.alertDialogState.collectAsStateWithLifecycle().value,
                            state = viewModel.state.collectAsStateWithLifecycle().value,
                            onEvent = { event ->
                                when(event){
                                    is SignUpEvent.OnBackClick-> {
                                        navController.popBackStack()
                                    }
                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            }
                        )
                    }
                }
            }
        }
    }
}

fun createUserScopeIfNeeded() {
    val koin = getKoin()
    if (koin.getScopeOrNull("USER_SESSION") == null) {
        koin.createScope(
            "USER_SESSION",
            named("USER_SCOPE")
        )
    }
}

@Serializable
object SplashScreen

@Serializable
object ScreenSignIn

@Serializable
object ScreenSignUp
