package com.example.studentregistration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
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
    private lateinit var courseSpinner: Spinner
    private lateinit var genderSelectionRadioGroup: RadioGroup
    private lateinit var activeStatusCheckbox: CheckBox
    private val courseList = listOf(Constants.COURSE1, Constants.COURSE2, Constants.COURSE3, Constants.COURSE4, Constants.COURSE5)

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
        courseSpinner = findViewById(R.id.courseSpinner)
        genderSelectionRadioGroup = findViewById(R.id.genderSelectionRadioGroup)
        activeStatusCheckbox = findViewById(R.id.studentActiveCheckbox)

        val courseAdapter = ArrayAdapter(this, R.layout.spinner_item, courseList)
        courseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        courseSpinner.adapter = courseAdapter

        val saveData = findViewById<Button>(R.id.saveData)

        if (studentId != -1L) {
            saveData.text = getString(R.string.update)
            setInputFieldsData()
        }

        saveData.setOnClickListener {
            if (validateStudentAddFields()) {
                handleSaveDataPress()
            }
        }
    }

    private fun setInputFieldsData() {
        lifecycleScope.launch {
            val student = studentDao.getStudentById(studentId!!)
            studentNameEditText.setText(student.name)
            studentAgeEditText.setText(student.age.toString())
            val selectedCourseIdx = courseList.indexOf(student.course)
            if (selectedCourseIdx != -1) {
                courseSpinner.setSelection(selectedCourseIdx)
            }
            if(student.status == Constants.ACTIVE) {
                activeStatusCheckbox.isChecked = true
            }
            for (index in 0 until genderSelectionRadioGroup.childCount) {
                val radioButton = genderSelectionRadioGroup.getChildAt(index) as RadioButton
                if (radioButton.text.toString() == student.gender) {
                    genderSelectionRadioGroup.check(radioButton.id)
                    break
                }
            }
        }
    }

    private fun validateStudentAddFields(): Boolean {
        val name = studentNameEditText.text.toString().trim()
        val age = studentAgeEditText.text.toString()
        val course = courseSpinner.selectedItem.toString()
        val genderId = genderSelectionRadioGroup.checkedRadioButtonId

        if(name.isBlank()) {
            showToastMessage(getString(R.string.name_error))
            return false
        }
        if (age.isBlank()) {
            showToastMessage(getString(R.string.age_error))
            return false
        }
        if (course.isBlank()) {
            showToastMessage(getString(R.string.course_error))
            return false
        }
        if (genderId == -1) {
            showToastMessage(getString(R.string.gender_error))
            return false
        }
        return true
    }

    private fun handleSaveDataPress() {
        val name = studentNameEditText.text.toString()
        val age = studentAgeEditText.text.toString()
        val gender = findViewById<RadioButton>(genderSelectionRadioGroup.checkedRadioButtonId).text.toString()
        val course = courseSpinner.selectedItem.toString()
        val status = if(activeStatusCheckbox.isChecked) Constants.ACTIVE else Constants.INACTIVE

        val studentKey = if(studentId != -1L) studentId ?: 0 else 0
        val student = Student(
            id = studentKey,
            name = name,
            age = age.toInt(),
            gender = gender,
            course = course,
            status = status
        )

        lifecycleScope.launch {
            if (studentId == -1L) {
                studentDao.insert(student)
            } else {
                studentDao.update(student)
            }
            finish()
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}