package com.example.moodymusic.ViewModel

import android.content.Context
import com.example.moodymusic.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodymusic.Injection
import com.example.moodymusic.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(context: Context):ViewModel() {
    private val userRepository:UserRepository = Injection.provideUserRepository(context)

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> get() = _firstName

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }

    fun loadFirstName() {
        viewModelScope.launch {
            _firstName.value = userRepository.getUserFirstName()
        }
    }

    fun logOut(){
        userRepository.logout()
    }

}