package com.example.tlucontact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
    private val filteredEmployeeList = mutableListOf<Employee>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EmployeeFragment", "onCreateView started")
        try {
            val view = inflater.inflate(R.layout.fragment_employee, container, false)

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEmployee)
            val searchView = view.findViewById<SearchView>(R.id.searchViewEmployee)
            val departmentSpinner = view.findViewById<Spinner>(R.id.spinner_department)
            val addEmployeeButton = view.findViewById<FloatingActionButton>(R.id.addEmployeeButton)

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            filteredEmployeeList.addAll(employeeList)
            employeeAdapter = EmployeeAdapter(filteredEmployeeList) { employee ->
                val dialog = EmployeeDetailActivity.newInstance(employee)
                dialog.show(parentFragmentManager, "EmployeeDetailDialog")
            }
            recyclerView.adapter = employeeAdapter
            Log.d("EmployeeFragment", "RecyclerView setup completed")

            if (FirebaseHelper.isAdmin()) {
                addEmployeeButton.visibility = View.VISIBLE
            } else {
                addEmployeeButton.visibility = View.GONE
            }


            addEmployeeButton.setOnClickListener {
                showAddEmployeeDialog()
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterEmployees(newText ?: "", departmentSpinner.selectedItem.toString())
                    return true
                }
            })


            departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedDepartment = parent?.getItemAtPosition(position).toString()
                    filterEmployees(searchView.query.toString(), selectedDepartment)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
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
            if (employees.isEmpty()) {
                Log.w("EmployeeFragment", "No employees loaded - check Firebase data or connection")
                Toast.makeText(requireContext(), "Failed to load employees. Check your connection or permissions.", Toast.LENGTH_LONG).show()
            } else {
                Log.d("EmployeeFragment", "Received ${employees.size} employees")
                Log.d("EmployeeFragment", "Employees loaded: ${employees.map { it.name }}")
                employeeList.clear()
                employeeList.addAll(employees)
                filterEmployees("", "Tất cả")
            }
        }
    }

    private fun filterEmployees(query: String, department: String) {
        filteredEmployeeList.clear()
        val searchQuery = query.lowercase()

        for (employee in employeeList) {
            val matchesSearch = searchQuery.isEmpty() ||
                    employee.name.lowercase().contains(searchQuery) ||
                    employee.position.lowercase().contains(searchQuery) ||
                    employee.department.lowercase().contains(searchQuery) ||
                    employee.phone.lowercase().contains(searchQuery) ||
                    employee.email.lowercase().contains(searchQuery)

            val matchesDepartment = department == "Tất cả" || employee.department == department

            if (matchesSearch && matchesDepartment) {
                filteredEmployeeList.add(employee)
            }
        }

        employeeAdapter.notifyDataSetChanged()
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
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = editName.text.toString().trim()
            val position = editPosition.text.toString().trim()
            val department = editDepartment.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val email = editEmail.text.toString().trim()

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
                email = email
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
                filterEmployees("", "Tất cả")
                Toast.makeText(requireContext(), "Added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeFragment", "Failed to add employee: ${e.message}")
                Toast.makeText(requireContext(), "Failed to add: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}