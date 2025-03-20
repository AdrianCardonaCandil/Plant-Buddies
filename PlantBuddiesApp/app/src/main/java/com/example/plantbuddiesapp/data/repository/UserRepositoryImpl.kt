package com.example.plantbuddiesapp.data.repository

import android.content.Context
import com.example.plantbuddiesapp.data.services.UserService
import com.example.plantbuddiesapp.domain.repository.UserRepository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val context: Context,
    private val userService: UserService,
    private val tokenManager: TokenManager
) : UserRepository {

}