package com.example.tlucontact

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.tlucontact.models.Department
import com.example.tlucontact.models.Employee

class DepartmentDetailActivity : DialogFragment() {

    private var department: Department? = null

    companion object {
        fun newInstance(department: Department): DepartmentDetailActivity {
            val fragment = DepartmentDetailActivity()
            val args = Bundle()
            args.putString("department_id", department.id)
            args.putString("department_name", department.name)
            args.putString("department_address", department.address)
            args.putString("department_phone", department.phone)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = arguments?.getString("department_id") ?: ""
        val name = arguments?.getString("department_name") ?: ""
        val phone = arguments?.getString("department_phone") ?: ""
        val address = arguments?.getString("department_address") ?: ""

        department = Department(id, name, address, phone)

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin phòng ban", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_department_detail, container, false)

        val detailName = view.findViewById<TextView>(R.id.tvUnitName)
        val detailPhone = view.findViewById<TextView>(R.id.tvUnitPhone)
        val detailAddress = view.findViewById<TextView>(R.id.tvUnitAddress)
        val btnCall = view.findViewById<Button>(R.id.tvbtnCall)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        detailName.text = "${department?.name}"
        detailPhone.text = "Số điện thoại: ${department?.phone}"
        detailAddress.text = "Địa chỉ: ${department?.address}"

        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${department?.phone}")
            startActivity(intent)
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        return view
    }
}