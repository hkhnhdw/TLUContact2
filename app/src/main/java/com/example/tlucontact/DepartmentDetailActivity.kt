package com.example.tlucontact

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.tlucontact.models.Department

class DepartmentDetailActivity : DialogFragment() {

    private var department: Department? = null

    companion object {
        fun newInstance(department: Department): DepartmentDetailActivity {
            val fragment = DepartmentDetailActivity()
            val args = Bundle()
            args.putParcelable("department", department)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_department_detail, container, false)


        department = arguments?.getParcelable("department")


        department?.let {
            view.findViewById<TextView>(R.id.tvUnitName).text = it.name
            view.findViewById<TextView>(R.id.tvUnitAddress).text = it.address
            view.findViewById<TextView>(R.id.tvUnitPhone).text = it.phone
        }

        view.findViewById<Button>(R.id.btnClose).setOnClickListener {
            dismiss()
        }

        return view
    }
}

private fun Bundle.putParcelable(key: String, department: Department) {
    putParcelable(key, department)
}

