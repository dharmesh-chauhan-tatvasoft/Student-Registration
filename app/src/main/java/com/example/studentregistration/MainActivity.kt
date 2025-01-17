package com.example.studentregistration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var db: RoomDB
    private lateinit var studentDao: StudentDao
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var studentsListView: RecyclerView
    private lateinit var floatingButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        db = RoomDB.getDatabase(this)
        studentDao = db.studentDao()

        studentsListView = findViewById(R.id.studentsList)
        studentsListView.layoutManager = LinearLayoutManager(this)

        floatingButton = findViewById(R.id.addStudentFloatButton)

        floatingButton.setOnClickListener {
            val intent = Intent(this, AddStudent::class.java)
            startActivity(intent)
        }

        loadStudents()
    }

    override fun onResume() {
        super.onResume()
        loadStudents()
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val students = studentDao.getAllStudents()
            studentAdapter = StudentAdapter(students,
                onUpdateClick = { student -> updateStudent(student) },
                onDeleteClick = { student -> deleteStudent(student) }
            )
            studentsListView.adapter = studentAdapter
        }
    }

    private fun updateStudent(student: Student) {
        val intent = Intent(this, AddStudent::class.java)
        intent.putExtra(Constants.STUDENT_ID, student.id)
        startActivity(intent)
    }

    private fun deleteStudent(student: Student) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                lifecycleScope.launch {
                    studentDao.delete(student)
                    loadStudents()
                    Toast.makeText(this@MainActivity, getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.no), null)
        builder.create().show()
    }
}