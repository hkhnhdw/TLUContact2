package com.example.tlucontact.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontact.R
import com.example.tlucontact.models.Department

class DepartmentAdapter(private val departmentList: List<Department>, private val onItemClick: (Department) -> Unit) :
    RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_department, parent, false)
        return DepartmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val department = departmentList[position]
        holder.bind(department)
    }

    override fun getItemCount(): Int = departmentList.size

    inner class DepartmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name_unit)
        private val addressTextView: TextView = itemView.findViewById(R.id.address_unit)

        init {
            itemView.setOnClickListener {
                val department = departmentList[adapterPosition]
                onItemClick(department)
            }
        }

        fun bind(department: Department) {
            nameTextView.text = department.name
            addressTextView.text = department.address
        }
    }
}