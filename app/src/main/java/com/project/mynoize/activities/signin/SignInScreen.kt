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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.signin.event.SignInEvent
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.activities.signin.ui.CustomPasswordTextField
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.activities.signin.ui.theme.MyNoizeTheme
import com.project.mynoize.core.presentation.asString

@Composable
fun SignInScreen(
    navController: NavController,
    alertDialogState: AlertDialogState,
    state: SignInState,
    onEvent: (SignInEvent) -> Unit
    ) {

    val localFocusManager = LocalFocusManager.current

    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                onEvent(SignInEvent.OnDismissAlertDialog)
            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }

    Column(
        Modifier.fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 50.dp)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ){


        Text(
            text="SIGN IN",
            Modifier.padding(top = 25.dp, start = 20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 33.sp,
            fontFamily = LatoFontFamily
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
                Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ){

                CustomTextField(
                    title = "EMAIL",
                    hintText = "Your email",
                    inputValue =  state.email,
                    onValueChange = {onEvent(SignInEvent.OnEmailChange(it))},
                    isError = state.emailError != null,
                    errorMessage = state.emailError?.asString() ?: ""
                )


                CustomPasswordTextField(
                    title = "PASSWORD",
                    hintText = "Your password",
                    inputValue = state.password,
                    onValueChange = {onEvent(SignInEvent.OnPasswordChange(it))},
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError?.asString() ?: ""
                )

                CustomButton (
                    modifier = Modifier,
                    text = "SIGN IN",
                    {
                        onEvent(SignInEvent.OnSignInClick)
                    }
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text="Don't have an account? Sign up",
                        modifier = Modifier.clickable{
                            navController.navigate(ScreenSignUp)
                        },
                        fontFamily = NovaSquareFontFamily,
                    )
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text="G",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.clickable{ onEvent(SignInEvent.OnSignInWithGoogleClick) }
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    MyNoizeTheme {
        val navController = rememberNavController()
        SignInScreen(navController,
            alertDialogState = AlertDialogState(),
            state = SignInState(),
            onEvent = {}
        )
    }
}