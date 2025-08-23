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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.mynoize.activities.signin.event.SignUpEvent
import com.project.mynoize.activities.signin.ui.MessageAlertDialog
import com.project.mynoize.activities.signin.ui.CustomButton
import com.project.mynoize.activities.signin.ui.CustomPasswordTextField
import com.project.mynoize.activities.signin.ui.CustomTextField
import com.project.mynoize.activities.signin.ui.theme.MyNoizeTheme


@Composable
fun SignUpScreen(navController: NavController){

    val vm = viewModel<SignUpViewModel>()

    val localFocusManager = LocalFocusManager.current


   if(vm.showAlertDialog){
       MessageAlertDialog(
           onDismiss = {
               vm.onEvent(SignUpEvent.OnDismissAlertDialog)
               if(vm.creatingAccount){navController.popBackStack()}
                       },
           message = vm.messageText
       )

   }

    Column (
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 12.dp)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ){
        Text(
            "Sign Up",
            Modifier.padding(top = 25.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )

        Spacer(Modifier.height(33.dp))

        if(vm.creatingAccount){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("Creating account...")
                CircularProgressIndicator()
            }
        }
        else {
            Column (
                Modifier.fillMaxSize()
            ){
                CustomTextField(
                    "Username",
                    "Enter your username",
                    vm.username,
                    onValueChange = {
                        vm.onEvent(SignUpEvent.OnUsernameChange(it))
                    }
                )

                CustomTextField(
                    "Email",
                    "Enter your email",
                    vm.email,
                    onValueChange = {
                        vm.onEvent(SignUpEvent.OnEmailChange(it))
                    }
                )

                CustomPasswordTextField(
                    "Password",
                    "Enter your password",
                    vm.password,
                    onValueChange = {
                        vm.onEvent(SignUpEvent.OnPasswordChange(it))
                    }
                )

                CustomPasswordTextField(
                    "Confirm password",
                    "Repeat your password",
                    vm.repeatedPassword,
                    onValueChange = {
                        vm.onEvent(SignUpEvent.OnRepeatedPasswordChange(it))
                    }
                )

                CustomButton(
                    text = "Sign Up",
                    {vm.onEvent(SignUpEvent.OnSignUpClick)}
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center

                ){
                    Text(
                        "Don't have an account? Sign up",
                        modifier = Modifier.clickable{
                            navController.popBackStack()
                        }
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
        val navController = rememberNavController()
        SignUpScreen(navController)
    }
}

