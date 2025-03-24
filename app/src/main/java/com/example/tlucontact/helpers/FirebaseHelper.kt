package com.example.tlucontact.helpers

import android.util.Log
import com.example.tlucontact.models.Department
import com.example.tlucontact.models.Employee
import com.google.firebase.database.*

object FirebaseHelper {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getDepartments(callback: (List<Department>) -> Unit) {
        Log.d("FirebaseHelper", "Starting to fetch departments")
        try {
            database.child("departments").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("FirebaseHelper", "Snapshot received: ${snapshot.exists()}")
                    if (!snapshot.exists()) {
                        Log.w("FirebaseHelper", "No departments found in snapshot")
                        callback(emptyList())
                        return
                    }
                    val departmentList = mutableListOf<Department>()
                    for (child in snapshot.children) {
                        try {
                            val department = child.getValue(Department::class.java)
                            if (department != null) {
                                Log.d("FirebaseHelper", "Parsed department: ${department.name}")
                                departmentList.add(department)
                            } else {
                                Log.w("FirebaseHelper", "Failed to parse department: ${child.key}, value: ${child.value}")
                            }
                        } catch (e: Exception) {
                            Log.e("FirebaseHelper", "Error parsing department ${child.key}: ${e.message}")
                        }
                    }
                    Log.d("FirebaseHelper", "Total departments fetched: ${departmentList.size}")
                    callback(departmentList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseHelper", "Failed to fetch departments: ${error.message}, code: ${error.code}")
                    callback(emptyList())
                }
            })
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Exception in getDepartments: ${e.message}")
            callback(emptyList())
        }
    }

    fun getEmployees(callback: (List<Employee>) -> Unit) {
        Log.d("FirebaseHelper", "Starting to fetch employees")
        try {
            database.child("employees").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("FirebaseHelper", "Snapshot received: ${snapshot.exists()}")
                    if (!snapshot.exists()) {
                        Log.w("FirebaseHelper", "No employees found in snapshot")
                        callback(emptyList())
                        return
                    }
                    val employeeList = mutableListOf<Employee>()
                    for (child in snapshot.children) {
                        try {
                            val employee = child.getValue(Employee::class.java)
                            if (employee != null) {
                                Log.d("FirebaseHelper", "Parsed employee: ${employee.name}")
                                employeeList.add(employee)
                            } else {
                                Log.w("FirebaseHelper", "Failed to parse employee: ${child.key}, value: ${child.value}")
                            }
                        } catch (e: Exception) {
                            Log.e("FirebaseHelper", "Error parsing employee ${child.key}: ${e.message}")
                        }
                    }
                    Log.d("FirebaseHelper", "Total employees fetched: ${employeeList.size}")
                    callback(employeeList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseHelper", "Failed to fetch employees: ${error.message}, code: ${error.code}")
                    callback(emptyList())
                }
            })
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Exception in getEmployees: ${e.message}")
            callback(emptyList())
        }
    }
}
