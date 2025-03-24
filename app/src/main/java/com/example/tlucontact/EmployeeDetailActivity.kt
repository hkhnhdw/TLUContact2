package com.example.tlucontact

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.tlucontact.models.Department

class EmployeeDetailActivity : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_employee_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val employeeName = arguments?.getString("employee_name")
        val employeePosition = arguments?.getString("employee_position")
        val employeeDepartment = arguments?.getString("employee_department")
        val employeePhone = arguments?.getString("employee_phone")
        val employeeEmail = arguments?.getString("employee_email")

        view.findViewById<TextView>(R.id.tvEmployeeName).text = employeeName
        view.findViewById<TextView>(R.id.tvEmployeePosition).text = "Chức vụ: $employeePosition"
        view.findViewById<TextView>(R.id.tvEmployeeUnit).text = "Cơ quan: $employeeDepartment"
        view.findViewById<TextView>(R.id.tvEmployeePhone).text = "Điện thoại: $employeePhone"
        view.findViewById<TextView>(R.id.tvEmployeeEmail).text = "Email: $employeeEmail"

        view.findViewById<Button>(R.id.btnClose).setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.DialogStyle)
    }

    companion object {
        fun newInstance(
            name: String,
            position: String,
            department: String,
            phone: String,
            email: String
        ): EmployeeDetailActivity {
            val args = Bundle()
            args.putString("employee_name", name)
            args.putString("employee_position", position)
            args.putString("employee_department", department)
            args.putString("employee_phone", phone)
            args.putString("employee_email", email)
            val fragment = EmployeeDetailActivity()
            fragment.arguments = args
            return fragment
        }
    }
}