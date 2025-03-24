package com.example.tlucontact.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontact.R
import com.example.tlucontact.models.Employee

class EmployeeAdapter(
    private val employeeList: List<Employee>,
    private val onItemClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_employee)
        val position: TextView = itemView.findViewById(R.id.position_employee)
        val phone: TextView = itemView.findViewById(R.id.phone_employee)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.name.text = employee.name
        holder.position.text = employee.position
        holder.phone.text = employee.phone

        holder.itemView.setOnClickListener {
            onItemClick(employee)
        }
    }

    override fun getItemCount(): Int = employeeList.size
}