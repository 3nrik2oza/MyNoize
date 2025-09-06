package com.project.mynoize.activities.signin.domain

import android.util.Patterns
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onError

class SignUpValidation {
    fun execute(username: String, email: String, password: String, repeatedPassword: String): EmptyResult<InputError.SignUp>{

        validateUsername(username = username).onError {
            return Result.Error(it)
        }

        validateEmail(email = email).onError {
            return Result.Error(it)
        }
        validatePassword(password = password).onError {
            return Result.Error(it)
        }

        validatePasswordRepeated(password = password, repeatedPassword = repeatedPassword).onError {
            return Result.Error(it)
        }

        return Result.Success(Unit)
    }

    private fun validateUsername(username: String): EmptyResult<InputError.SignUp>{
        return if(username.isEmpty()){
            Result.Error(InputError.SignUp.ENTER_USERNAME)
        }else if(username.length > 30){
            Result.Error(InputError.SignUp.USERNAME_TOO_LONG)
        } else Result.Success(Unit)
    }

    private fun validateEmail(email: String): EmptyResult<InputError.SignUp>{
        return if(email.isEmpty()){
            Result.Error(InputError.SignUp.ENTER_EMAIL)
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Result.Error(InputError.SignUp.INCORRECT_EMAIL)
        }
        else Result.Success(Unit)
    }

    private fun validatePassword(password: String): EmptyResult<InputError.SignUp>{
        return if(password.isEmpty()){
            Result.Error(InputError.SignUp.ENTER_PASSWORD)
        }else if(password.length < 8){
            Result.Error(InputError.SignUp.PASSWORD_TOO_SHORT)
        }else if(password.length > 30){
            Result.Error(InputError.SignUp.PASSWORD_TOO_LONG)
        } else if(!password.any { it.isDigit() }){
            Result.Error(InputError.SignUp.PASSWORD_NO_NUMBERS)
        } else if(!password.any { it.isLetter() }){
            Result.Error(InputError.SignUp.PASSWORD_NO_LETTERS)
        }
        else Result.Success(Unit)
    }

    private fun validatePasswordRepeated(password: String, repeatedPassword: String): EmptyResult<InputError.SignUp>{
        return if(repeatedPassword.isEmpty()){
            Result.Error(InputError.SignUp.ENTER_REPEATED_PASSWORD)
        }else if(repeatedPassword != password){
            Result.Error(InputError.SignUp.PASSWORDS_DO_NOT_MATCH)
        }else Result.Success(Unit)
    }

}