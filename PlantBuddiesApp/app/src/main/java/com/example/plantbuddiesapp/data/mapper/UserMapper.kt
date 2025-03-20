package com.example.plantbuddiesapp.data.mapper

import com.example.plantbuddiesapp.data.dto.UserDto
import com.example.plantbuddiesapp.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        uid = uid,
        email = email,
        name = name,
        country = country,
        description = description,
        imageUrl = imageUrl,
        plants = plants.map { it.toDomain() }
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        email = email,
        name = name,
        country = country,
        description = description,
        imageUrl = imageUrl,
        plants = plants.map { it.toDto() }
    )
}