package com.example.studentregistration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView


class StudentAdapter(
    private val students: List<Student>,
    private val onUpdateClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: MaterialTextView = itemView.findViewById(R.id.nameText)
        private val ageText: MaterialTextView = itemView.findViewById(R.id.ageText)
        private val genderText: MaterialTextView = itemView.findViewById(R.id.genderText)
        private val courseText: MaterialTextView = itemView.findViewById(R.id.courseText)
        private val updateButton: Button = itemView.findViewById(R.id.updateButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        fun bind(student: Student) {
            nameText.text = student.name
            ageText.text = student.age.toString()
            genderText.text = student.gender
            courseText.text = student.course

            updateButton.setOnClickListener {
                onUpdateClick(student)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(student)
            }
        }
    }
}
