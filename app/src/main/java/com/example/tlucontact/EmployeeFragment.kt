package com.example.tlucontact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontact.adapters.EmployeeAdapter
import com.example.tlucontact.helpers.FirebaseHelper
import com.example.tlucontact.models.Employee
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class EmployeeFragment : Fragment() {

    private lateinit var employeeAdapter: EmployeeAdapter
    private val employeeList = mutableListOf<Employee>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EmployeeFragment", "onCreateView started")
        try {
            val view = inflater.inflate(R.layout.fragment_employee, container, false)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEmployee)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            employeeAdapter = EmployeeAdapter(employeeList) { employee ->
                showActionDialog(employee)
            }
            recyclerView.adapter = employeeAdapter
            Log.d("EmployeeFragment", "RecyclerView setup completed")

            // Xử lý nút thêm nhân viên
            val addEmployeeButton = view.findViewById<FloatingActionButton>(R.id.addEmployeeButton)
            addEmployeeButton.setOnClickListener {
                showAddEmployeeDialog()
            }

            loadEmployees()
            return view
        } catch (e: Exception) {
            Log.e("EmployeeFragment", "Error in onCreateView: ${e.message}")
            return null
        }
    }

    private fun loadEmployees() {
        Log.d("EmployeeFragment", "Loading employees from Firebase")
        FirebaseHelper.getEmployees { employees ->
            Log.d("EmployeeFragment", "Received ${employees.size} employees")
            employeeList.clear()
            employeeList.addAll(employees)
            employeeAdapter.notifyDataSetChanged()
            if (employees.isEmpty()) {
                Log.w("EmployeeFragment", "No employees loaded - check Firebase data or connection")
            } else {
                Log.d("EmployeeFragment", "Employees loaded: ${employees.map { it.name }}")
            }
        }
    }

    private fun showAddEmployeeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_employee, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editPosition = dialogView.findViewById<EditText>(R.id.editPosition)
        val editDepartment = dialogView.findViewById<EditText>(R.id.editDepartment)
        val editPhone = dialogView.findViewById<EditText>(R.id.editPhone)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)
        val editAvatarUrl = dialogView.findViewById<EditText>(R.id.editAvatarUrl)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = editName.text.toString().trim()
            val position = editPosition.text.toString().trim()
            val department = editDepartment.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val avatarUrl = editAvatarUrl.text.toString().trim()

            if (name.isEmpty() || position.isEmpty() || department.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newId = FirebaseDatabase.getInstance().reference.child("employees").push().key ?: return@setOnClickListener
            val newEmployee = Employee(
                id = newId,
                name = name,
                position = position,
                department = department,
                phone = phone,
                email = email,
                avatarUrl = avatarUrl
            )

            addEmployee(newEmployee)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addEmployee(employee: Employee) {
        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.id)
        database.setValue(employee)
            .addOnSuccessListener {
                Log.d("EmployeeFragment", "Added employee: ${employee.name}")
                employeeList.add(employee)
                employeeAdapter.notifyItemInserted(employeeList.size - 1)
                Toast.makeText(requireContext(), "Added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeFragment", "Failed to add employee: ${e.message}")
                Toast.makeText(requireContext(), "Failed to add: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showActionDialog(employee: Employee) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_employee_actions, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editButton = dialogView.findViewById<Button>(R.id.editButton)
        val deleteButton = dialogView.findViewById<Button>(R.id.deleteButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        editButton.setOnClickListener {
            dialog.dismiss()
            showEditEmployeeDialog(employee)
        }

        deleteButton.setOnClickListener {
            dialog.dismiss()
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete ${employee.name}?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteEmployee(employee)
                }
                .setNegativeButton("No", null)
                .show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditEmployeeDialog(employee: Employee) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_employee, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editPosition = dialogView.findViewById<EditText>(R.id.editPosition)
        val editDepartment = dialogView.findViewById<EditText>(R.id.editDepartment)
        val editPhone = dialogView.findViewById<EditText>(R.id.editPhone)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)
        val editAvatarUrl = dialogView.findViewById<EditText>(R.id.editAvatarUrl)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        editName.setText(employee.name)
        editPosition.setText(employee.position)
        editDepartment.setText(employee.department)
        editPhone.setText(employee.phone)
        editEmail.setText(employee.email)
        editAvatarUrl.setText(employee.avatarUrl)

        saveButton.setOnClickListener {
            val updatedEmployee = employee.copy(
                name = editName.text.toString().trim(),
                position = editPosition.text.toString().trim(),
                department = editDepartment.text.toString().trim(),
                phone = editPhone.text.toString().trim(),
                email = editEmail.text.toString().trim(),
                avatarUrl = editAvatarUrl.text.toString().trim()
            )

            updateEmployee(updatedEmployee)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateEmployee(employee: Employee) {
        val position = employeeList.indexOfFirst { it.id == employee.id }
        if (position == -1) return

        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.id)
        database.setValue(employee)
            .addOnSuccessListener {
                Log.d("EmployeeFragment", "Updated employee: ${employee.name}")
                employeeList[position] = employee
                employeeAdapter.notifyItemChanged(position)
                Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeFragment", "Failed to update employee: ${e.message}")
                Toast.makeText(requireContext(), "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteEmployee(employee: Employee) {
        val position = employeeList.indexOfFirst { it.id == employee.id }
        if (position == -1) return

        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.id)
        database.removeValue()
            .addOnSuccessListener {
                Log.d("EmployeeFragment", "Deleted employee: ${employee.name}")
                employeeList.removeAt(position)
                employeeAdapter.notifyItemRemoved(position)
                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeFragment", "Failed to delete employee: ${e.message}")
                Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}