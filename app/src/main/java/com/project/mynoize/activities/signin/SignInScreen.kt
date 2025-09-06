package com.project.mynoize.activities.signin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.mynoize.activities.main.state.AlertDialogState
import com.project.mynoize.activities.signin.event.SignInEvent
import com.project.mynoize.activities.signin.ui.MessageAlertDialog
import com.project.mynoize.activities.signin.ui.CustomButton
import com.project.mynoize.activities.signin.ui.CustomPasswordTextField
import com.project.mynoize.activities.signin.ui.CustomTextField
import com.project.mynoize.activities.signin.ui.theme.MyNoizeTheme
import com.project.mynoize.core.presentation.asString

@Composable
fun SignInScreen(
    navController: NavController,
    onSignInWithGoogleClick: () -> Unit,
    onSuccessfulSignInWithEmail: () -> Unit,
    //vm: SignInViewModel
    alertDialogState: AlertDialogState,
    state: SignInState,
    onEvent: (SignInEvent) -> Unit
    ) {

    val localFocusManager = LocalFocusManager.current
    //val alertDialogState by vm.alertDialogState.collectAsState()
    //val state by vm.state.collectAsStateWithLifecycle()

    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                onEvent(SignInEvent.OnDismissAlertDialog)
            },
            message = alertDialogState.message?.asString() ?: ""
        )
    }

    Column(
        Modifier.fillMaxSize()
            .padding(horizontal = 12.dp)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ){


        Text(
            "Sign In",
            Modifier.padding(top = 25.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 33.sp
        )

        Spacer(Modifier.height(31.dp))

        if(state.loading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }else{
            Column(
                Modifier.fillMaxWidth()
            ){

                CustomTextField(
                    "Email",
                    "Your email",
                    state.email,
                    onValueChange = {onEvent(SignInEvent.OnEmailChange(it))},
                    isError = state.emailError != null,
                    errorMessage = state.emailError?.asString() ?: ""
                )


                CustomPasswordTextField(
                    "Password",
                    "Your password",
                    state.password,
                    onValueChange = {onEvent(SignInEvent.OnPasswordChange(it))},
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError?.asString() ?: ""
                )

                CustomButton (
                    text = "Sign In",
                    {
                        onEvent(SignInEvent.OnSignInClick(onSuccessfulSignInWithEmail))
                    }
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        "Don't have an account? Sign up",
                        modifier = Modifier.clickable{
                            navController.navigate(ScreenSignUp)
                        }
                    )
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    OutlinedButton(
                        onClick = {onSignInWithGoogleClick()},
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(
                            "G",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    MyNoizeTheme {
        val vm = viewModel<SignInViewModel>()
        val navController = rememberNavController()
        SignInScreen(navController,
            {},
            {},
            alertDialogState = AlertDialogState(),
            state = SignInState(),
            onEvent = {}
        )
    }
}