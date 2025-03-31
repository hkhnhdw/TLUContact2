package com.example.tlucontact

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.tlucontact.models.Department
import com.example.tlucontact.models.Employee
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EmployeeDetailActivity : DialogFragment() {

    private lateinit var employee: Employee
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserEmail: String?
        get() = auth.currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = arguments?.getString("EMPLOYEE_ID") ?: ""
        val name = arguments?.getString("EMPLOYEE_NAME") ?: ""
        val position = arguments?.getString("EMPLOYEE_POSITION") ?: ""
        val department = arguments?.getString("EMPLOYEE_DEPARTMENT") ?: ""
        val phone = arguments?.getString("EMPLOYEE_PHONE") ?: ""
        val email = arguments?.getString("EMPLOYEE_EMAIL") ?: ""
        val avatarUrl = arguments?.getString("EMPLOYEE_AVATAR_URL") ?: ""


        employee = Employee(id, name, position, department, phone, email, avatarUrl)

        if (id.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_employee_detail, container, false)

        val detailName = view.findViewById<TextView>(R.id.tvEmployeeName)
        val detailPosition = view.findViewById<TextView>(R.id.tvEmployeePosition)
        val detailDepartment = view.findViewById<TextView>(R.id.tvEmployeeUnit)
        val detailPhone = view.findViewById<TextView>(R.id.tvEmployeePhone)
        val detailEmail = view.findViewById<TextView>(R.id.tvEmployeeEmail)
        val editButton = view.findViewById<Button>(R.id.editButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val backButton = view.findViewById<Button>(R.id.btnClose)
        val callButton = view.findViewById<Button>(R.id.btnCall)

        detailName.text = "Tên: ${employee.name}"
        detailPosition.text = "Chức vụ: ${employee.position}"
        detailDepartment.text = "Phòng ban: ${employee.department}"
        detailPhone.text = "Số điện thoại: ${employee.phone}"
        detailEmail.text = "Email: ${employee.email}"


        val canEdit = currentUserEmail != null && currentUserEmail == employee.email
        Log.d("EmployeeDetailActivity", "Checking edit permission: currentUserEmail=$currentUserEmail, employeeEmail=${employee.email}, canEdit=$canEdit")

        if (canEdit) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        } else {
            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        }

        editButton.setOnClickListener {
            showEditEmployeeDialog()
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Xóa Nhân viên")
                .setMessage("Bạn có chắc chắn muốn xóa ${employee.name}?")
                .setPositiveButton("Có") { _, _ ->
                    deleteEmployee()
                }
                .setNegativeButton("Không", null)
                .show()
        }

        backButton.setOnClickListener {
            dismiss()
        }

        callButton.setOnClickListener {
            val phoneNumber = employee.phone
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        return view
    }

    private fun showEditEmployeeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_employee, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editPosition = dialogView.findViewById<EditText>(R.id.editPosition)
        val editDepartment = dialogView.findViewById<EditText>(R.id.editDepartment)
        val editPhone = dialogView.findViewById<EditText>(R.id.editPhone)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        editName.setText(employee.name)
        editPosition.setText(employee.position)
        editDepartment.setText(employee.department)
        editPhone.setText(employee.phone)
        editEmail.setText(employee.email)

        saveButton.setOnClickListener {
            val updatedEmployee = employee.copy(
                name = editName.text.toString().trim(),
                position = editPosition.text.toString().trim(),
                department = editDepartment.text.toString().trim(),
                phone = editPhone.text.toString().trim(),
                email = editEmail.text.toString().trim()
            )

            updateEmployee(updatedEmployee)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateEmployee(updatedEmployee: Employee) {
        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.id)
        database.setValue(updatedEmployee)
            .addOnSuccessListener {
                Log.d("EmployeeDetailActivity", "Updated employee: ${updatedEmployee.name}")
                employee = updatedEmployee

                view?.findViewById<TextView>(R.id.tvEmployeeName)?.text = "Tên: ${employee.name}"
                view?.findViewById<TextView>(R.id.tvEmployeePosition)?.text = "Chức vụ: ${employee.position}"
                view?.findViewById<TextView>(R.id.tvEmployeeUnit)?.text = "Phòng ban: ${employee.department}"
                view?.findViewById<TextView>(R.id.tvEmployeePhone)?.text = "Số điện thoại: ${employee.phone}"
                view?.findViewById<TextView>(R.id.tvEmployeeEmail)?.text = "Email: ${employee.email}"
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeDetailActivity", "Failed to update employee: ${e.message}")
                Toast.makeText(requireContext(), "Cập nhật thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteEmployee() {
        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.id)
        database.removeValue()
            .addOnSuccessListener {
                Log.d("EmployeeDetailActivity", "Deleted employee: ${employee.name}")
                Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeDetailActivity", "Failed to delete employee: ${e.message}")
                Toast.makeText(requireContext(), "Xóa thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(employee: Employee): EmployeeDetailActivity {
            val fragment = EmployeeDetailActivity()
            val args = Bundle()
            args.putString("EMPLOYEE_ID", employee.id)
            args.putString("EMPLOYEE_NAME", employee.name)
            args.putString("EMPLOYEE_POSITION", employee.position)
            args.putString("EMPLOYEE_DEPARTMENT", employee.department)
            args.putString("EMPLOYEE_PHONE", employee.phone)
            args.putString("EMPLOYEE_EMAIL", employee.email)
            args.putString("EMPLOYEE_AVATAR_URL", employee.avatarUrl)
            fragment.arguments = args
            return fragment
        }
    }
}