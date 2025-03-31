package com.example.tlucontact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontact.adapters.DepartmentAdapter
import com.example.tlucontact.helpers.FirebaseHelper
import com.example.tlucontact.models.Department

class DepartmentFragment : Fragment() {

    private lateinit var departmentAdapter: DepartmentAdapter
    private val departmentList = mutableListOf<Department>()
    private val filteredDepartmentList = mutableListOf<Department>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("DepartmentFragment", "onCreateView started")
        try {
            val view = inflater.inflate(R.layout.fragment_department, container, false)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewDepartment)
            val searchView = view.findViewById<SearchView>(R.id.searchViewDepartment)

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            filteredDepartmentList.addAll(departmentList)
            departmentAdapter = DepartmentAdapter(filteredDepartmentList) { department ->
                val dialog = DepartmentDetailActivity.newInstance(department)
                dialog.show(parentFragmentManager, "DepartmentDetailDialog")
            }
            recyclerView.adapter = departmentAdapter
            Log.d("DepartmentFragment", "RecyclerView setup completed")

            // Xử lý SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterDepartments(newText ?: "")
                    return true
                }
            })

            loadDepartments()
            return view
        } catch (e: Exception) {
            Log.e("DepartmentFragment", "Error in onCreateView: ${e.message}")
            return null
        }
    }

    private fun loadDepartments() {
        Log.d("DepartmentFragment", "Loading departments from Firebase")
        FirebaseHelper.getDepartments { departments ->
            Log.d("DepartmentFragment", "Received ${departments.size} departments")
            departmentList.clear()
            departmentList.addAll(departments)
            filterDepartments("")
            if (departments.isEmpty()) {
                Log.w("DepartmentFragment", "No departments loaded - check Firebase data or connection")
            } else {
                Log.d("DepartmentFragment", "Departments loaded: ${departments.map { it.name }}")
            }
        }
    }

    private fun filterDepartments(query: String) {
        filteredDepartmentList.clear()
        val searchQuery = query.lowercase()

        for (department in departmentList) {
            if (searchQuery.isEmpty() || department.name.lowercase().contains(searchQuery)) {
                filteredDepartmentList.add(department)
            }
        }

        departmentAdapter.notifyDataSetChanged()
    }
}