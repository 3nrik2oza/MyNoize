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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import com.project.mynoize.activities.signin.event.SignUpEvent
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.activities.signin.ui.CustomPasswordTextField
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.activities.signin.ui.theme.MyNoizeTheme
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.asString


@Composable
fun SignUpScreen(
    alertDialogState: AlertDialogState,
    state: SignUpState,
    onEvent: (SignUpEvent) -> Unit
){

    val localFocusManager = LocalFocusManager.current


   if(alertDialogState.show){
       MessageAlertDialog(
           onDismiss = {
               onEvent(SignUpEvent.OnDismissAlertDialog)
               if(!alertDialogState.warning){
                   onEvent(SignUpEvent.OnBackClick)
               } },
           message = alertDialogState.message?.asString() ?: "",
           warning = alertDialogState.warning
       )

   }

    Column (
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 50.dp)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ){
        Text(
            "SIGN UP",
            Modifier.padding(top = 25.dp, start = 20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )

        Spacer(Modifier.height(33.dp))

        if(state.creatingAccount){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text(
                    "Creating account...",
                    fontFamily = NovaSquareFontFamily
                )

                CircularProgressIndicator(
                    color = Red
                )
            }
        }
        else {
            Column (
                Modifier.fillMaxSize().padding(horizontal = 20.dp)
            ){
                CustomTextField(
                    title="Username",
                    hintText = "Enter your username",
                    inputValue = state.username,
                    onValueChange = {
                        onEvent(SignUpEvent.OnUsernameChange(it))
                    },
                    isError = state.usernameError != null,
                    errorMessage = state.usernameError?.asString() ?: ""
                )

                CustomTextField(
                    title = "Email",
                    hintText = "Enter your email",
                    inputValue = state.email,
                    onValueChange = {
                        onEvent(SignUpEvent.OnEmailChange(it))
                    },
                    isError = state.emailError != null,
                    errorMessage = state.emailError?.asString() ?: ""
                )

                CustomPasswordTextField(
                    title = "Password",
                    hintText = "Enter your password",
                    inputValue = state.password,
                    onValueChange = {
                        onEvent(SignUpEvent.OnPasswordChange(it))
                    },
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError?.asString() ?: ""
                )

                CustomPasswordTextField(
                    title = "Confirm password",
                    hintText = "Repeat your password",
                    inputValue = state.repeatedPassword,
                    onValueChange = {
                        onEvent(SignUpEvent.OnRepeatedPasswordChange(it))
                    },
                    isError = state.repeatedPasswordError != null,
                    errorMessage = state.repeatedPasswordError?.asString() ?: ""
                )

                CustomButton(
                    modifier = Modifier,
                    text = "SIGN UP",
                    {onEvent(SignUpEvent.OnSignUpClick)}
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center

                ){
                    Text(
                        text = "You already have an account? Sign in",
                        modifier = Modifier.clickable{
                            onEvent(SignUpEvent.OnBackClick)
                        },
                        fontFamily = NovaSquareFontFamily
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    MyNoizeTheme {

        SignUpScreen(
            alertDialogState = AlertDialogState(),
            state = SignUpState(),
            onEvent = {}
        )
    }
}

