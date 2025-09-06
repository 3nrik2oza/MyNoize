package com.project.mynoize.activities.signin.domain

import android.util.Patterns
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onError

class SignInValidation {
    fun execute(email: String, password: String): EmptyResult<InputError.SingIn>{

        validateEmail(email = email).onError {
            return Result.Error(it)
        }
        validatePassword(password = password).onError {
            return Result.Error(it)
        }


        return Result.Success(Unit)
    }

    private fun validateEmail(email: String): EmptyResult<InputError.SingIn>{
        return if(email.isEmpty()){
            Result.Error(InputError.SingIn.ENTER_EMAIL)
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Result.Error(InputError.SingIn.INCORRECT_EMAIL)
        }
        else Result.Success(Unit)
    }

    private fun validatePassword(password: String): EmptyResult<InputError.SingIn>{
        return if(password.isEmpty()){
            Result.Error(InputError.SingIn.ENTER_PASSWORD)
        }else Result.Success(Unit)
    }

}