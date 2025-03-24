package com.example.tlucontact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontact.adapters.EmployeeAdapter
import com.example.tlucontact.helpers.FirebaseHelper
import com.example.tlucontact.models.Employee

class EmployeeFragment : Fragment() {

    private lateinit var employeeAdapter: EmployeeAdapter
    private val employeeList = mutableListOf<Employee>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EmployeeFragment", "onCreateView started")
        val view = inflater.inflate(R.layout.fragment_employee, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEmployee)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        employeeAdapter = EmployeeAdapter(employeeList) { employee ->
            Log.d("EmployeeFragment", "Clicked employee: ${employee.name}")
            val detailFragment = EmployeeDetailActivity.newInstance(
                employee.name, employee.position, employee.department, employee.phone, employee.email
            )
            detailFragment.show(parentFragmentManager, "EmployeeDetail")
        }
        recyclerView.adapter = employeeAdapter
        loadEmployees()
        Log.d("EmployeeFragment", "onCreateView completed")
        return view
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
}