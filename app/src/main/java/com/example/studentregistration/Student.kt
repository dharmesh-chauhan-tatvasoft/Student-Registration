package com.example.studentregistration

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Constants.STUDENTS)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String,
    val course: String,
    val status: String
)