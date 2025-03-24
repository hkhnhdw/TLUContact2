package com.example.tlucontact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontact.adapters.DepartmentAdapter
import com.example.tlucontact.helpers.FirebaseHelper
import com.example.tlucontact.models.Department

class DepartmentFragment : Fragment() {

    private lateinit var departmentAdapter: DepartmentAdapter
    private val departmentList = mutableListOf<Department>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("DepartmentFragment", "onCreateView started")
        val view = inflater.inflate(R.layout.fragment_department, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewDepartment)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        departmentAdapter = DepartmentAdapter(departmentList) { department ->
            Log.d("DepartmentFragment", "Clicked department: ${department.name}")
            val detailFragment = DepartmentDetailActivity.newInstance(department)
            detailFragment.show(parentFragmentManager, "DepartmentDetail")
        }
        recyclerView.adapter = departmentAdapter
        loadDepartments()
        Log.d("DepartmentFragment", "onCreateView completed")
        return view
    }

    private fun loadDepartments() {
        Log.d("DepartmentFragment", "Loading departments from Firebase")
        FirebaseHelper.getDepartments { departments ->
            Log.d("DepartmentFragment", "Received ${departments.size} departments")
            departmentList.clear()
            departmentList.addAll(departments)
            departmentAdapter.notifyDataSetChanged()
            if (departments.isEmpty()) {
                Log.w("DepartmentFragment", "No departments loaded - check Firebase data or connection")
            } else {
                Log.d("DepartmentFragment", "Departments loaded: ${departments.map { it.name }}")
            }
        }
    }
}