package com.example.studentregistration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddStudent : AppCompatActivity() {
    private lateinit var db: RoomDB
    private lateinit var studentDao: StudentDao
    private var studentId: Long? = null
    private lateinit var studentNameEditText: TextInputEditText
    private lateinit var studentAgeEditText: TextInputEditText
    private lateinit var studentCourseEditText: TextInputEditText
    private lateinit var genderSelectionRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)
        setupAddStudentInputFields()
    }

    private fun setupAddStudentInputFields() {
        db = RoomDB.getDatabase(this)
        studentDao = db.studentDao()

        studentId = intent.getLongExtra(Constants.STUDENT_ID, -1)

        studentNameEditText = findViewById(R.id.studentNameEditText)
        studentAgeEditText = findViewById(R.id.studentAgeEditText)
        studentCourseEditText = findViewById(R.id.studentCourseEditText)
        genderSelectionRadioGroup = findViewById(R.id.genderSelectionRadioGroup)


        if (studentId != -1L) {
            lifecycleScope.launch {
                val student = studentDao.getStudentById(studentId!!)
                studentNameEditText.setText(student.name)
                studentAgeEditText.setText(student.age.toString())
                studentCourseEditText.setText(student.course)
            }
        }

        val saveData = findViewById<Button>(R.id.saveData)
        saveData.setOnClickListener {
            handleSaveDataPress()
        }
    }

    private fun handleSaveDataPress() {
        val name = studentNameEditText.text.toString()
        val age = studentAgeEditText.text.toString().toIntOrNull()
        val gender = findViewById<RadioButton>(genderSelectionRadioGroup.checkedRadioButtonId).text.toString()
        val course = studentCourseEditText.text.toString()

        if (name.isNotEmpty() && age != null && course.isNotEmpty()) {

            val studentKey = if(studentId != -1L){studentId ?: 0}else 0
            val student = Student(
                id = studentKey,
                name = name,
                age = age,
                gender = gender,
                course = course
            )

            lifecycleScope.launch {
                if (studentId == -1L) {
                    studentDao.insert(student)
                } else {
                    studentDao.update(student)
                }
                finish()
            }
        } else {
            Toast.makeText(this, getString(R.string.input_error), Toast.LENGTH_SHORT).show()
        }
    }
}