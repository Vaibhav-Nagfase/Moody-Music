package com.example.moodymusic

import android.content.Context
import com.example.moodymusic.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Injection {
    private val instance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun instance(): FirebaseFirestore {
        return instance
    }

    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), context)
    }
}