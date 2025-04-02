package com.example.tlucontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.tlucontact.helpers.FirebaseHelper
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

        // Lấy dữ liệu nhân viên từ arguments
        val id = arguments?.getString("EMPLOYEE_ID") ?: ""
        val name = arguments?.getString("EMPLOYEE_NAME") ?: ""
        val position = arguments?.getString("EMPLOYEE_POSITION") ?: ""
        val department = arguments?.getString("EMPLOYEE_DEPARTMENT") ?: ""
        val phone = arguments?.getString("EMPLOYEE_PHONE") ?: ""
        val email = arguments?.getString("EMPLOYEE_EMAIL") ?: ""
        val avatarUrl = arguments?.getString("EMPLOYEE_AVATAR_URL") ?: ""

        employee = Employee(id, name, position, department, phone, email, avatarUrl)

        // Kiểm tra nếu không có ID, đóng dialog
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
        // Nạp layout cho dialog
        val view = inflater.inflate(R.layout.activity_employee_detail, container, false)

        // Ánh xạ các thành phần giao diện
        val detailName = view.findViewById<TextView>(R.id.tvEmployeeName)
        val detailPosition = view.findViewById<TextView>(R.id.tvEmployeePosition)
        val detailDepartment = view.findViewById<TextView>(R.id.tvEmployeeUnit)
        val detailPhone = view.findViewById<TextView>(R.id.tvEmployeePhone)
        val detailEmail = view.findViewById<TextView>(R.id.tvEmployeeEmail)
        val editButton = view.findViewById<Button>(R.id.editButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val backButton = view.findViewById<Button>(R.id.btnClose)
        val callButton = view.findViewById<Button>(R.id.btnCall)

        // Hiển thị thông tin nhân viên
        detailName.text = "Tên: ${employee.name}"
        detailPosition.text = "Chức vụ: ${employee.position}"
        detailDepartment.text = "Phòng ban: ${employee.department}"
        detailPhone.text = "Số điện thoại: ${employee.phone}"
        detailEmail.text = "Email: ${employee.email}"

        // Kiểm tra quyền
        val isAdmin = FirebaseHelper.isAdmin()
        val canEdit = currentUserEmail != null && currentUserEmail == employee.email

        Log.d("EmployeeDetailActivity", "Checking permissions: isAdmin=$isAdmin, currentUserEmail=$currentUserEmail, employeeEmail=${employee.email}, canEdit=$canEdit")

        // Quyền admin: Có thể chỉnh sửa và xóa bất kỳ nhân viên nào
        if (isAdmin) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        }
        // Quyền nhân viên: Chỉ được chỉnh sửa thông tin của chính mình, không được xóa
        else if (canEdit) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.GONE
        }
        // Người dùng khác: Không có quyền chỉnh sửa hoặc xóa
        else {
            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        }

        // Xử lý sự kiện nút "Sửa"
        editButton.setOnClickListener {
            showEditEmployeeDialog()
        }

        // Xử lý sự kiện nút "Xóa"
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

        // Xử lý sự kiện nút "Đóng"
        backButton.setOnClickListener {
            dismiss()
        }

        // Xử lý sự kiện nút "Gọi"
        callButton.setOnClickListener {
            val phoneNumber = employee.phone
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        return view
    }

    private fun showEditEmployeeDialog() {
        // Nạp layout cho dialog chỉnh sửa
        val dialogView = layoutInflater.inflate(R.layout.dialog_employee, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Ánh xạ các thành phần trong dialog
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editPosition = dialogView.findViewById<EditText>(R.id.editPosition)
        val editDepartment = dialogView.findViewById<EditText>(R.id.editDepartment)
        val editPhone = dialogView.findViewById<EditText>(R.id.editPhone)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        // Điền thông tin hiện tại vào các trường
        editName.setText(employee.name)
        editPosition.setText(employee.position)
        editDepartment.setText(employee.department)
        editPhone.setText(employee.phone)
        editEmail.setText(employee.email)

        // Vô hiệu hóa trường email vì email là key
        editEmail.isEnabled = false

        // Xử lý sự kiện nút "Lưu"
        saveButton.setOnClickListener {
            val name = editName.text.toString().trim()
            val position = editPosition.text.toString().trim()
            val department = editDepartment.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val email = editEmail.text.toString().trim()

            // Kiểm tra các trường không được để trống
            if (name.isEmpty() || position.isEmpty() || department.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra định dạng số điện thoại
            if (!phone.matches(Regex("\\d+"))) {
                Toast.makeText(requireContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra định dạng email (dù không cho chỉnh sửa)
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo đối tượng nhân viên mới với thông tin đã chỉnh sửa
            val updatedEmployee = Employee(
                id = employee.id,
                name = name,
                position = position,
                department = department,
                phone = phone,
                email = email,
                avatarUrl = employee.avatarUrl
            )

            // Cập nhật nhân viên lên Firebase
            updateEmployee(updatedEmployee)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateEmployee(updatedEmployee: Employee) {
        // Sử dụng email làm key để cập nhật nhân viên
        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.email)
        database.setValue(updatedEmployee)
            .addOnSuccessListener {
                Log.d("EmployeeDetailActivity", "Updated employee: ${updatedEmployee.name}")
                employee = updatedEmployee

                // Cập nhật giao diện
                view?.findViewById<TextView>(R.id.tvEmployeeName)?.text = "Tên: ${employee.name}"
                view?.findViewById<TextView>(R.id.tvEmployeePosition)?.text = "Chức vụ: ${employee.position}"
                view?.findViewById<TextView>(R.id.tvEmployeeUnit)?.text = "Phòng ban: ${employee.department}"
                view?.findViewById<TextView>(R.id.tvEmployeePhone)?.text = "Số điện thoại: ${employee.phone}"
                view?.findViewById<TextView>(R.id.tvEmployeeEmail)?.text = "Email: ${employee.email}"
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeDetailActivity", "Failed to update employee: ${e.message}")
                Toast.makeText(requireContext(), "Cập nhật thất bại: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteEmployee() {
        // Sử dụng email làm key để xóa nhân viên
        val database = FirebaseDatabase.getInstance().reference.child("employees").child(employee.email)
        database.removeValue()
            .addOnSuccessListener {
                Log.d("EmployeeDetailActivity", "Deleted employee: ${employee.name}")
                Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener { e ->
                Log.e("EmployeeDetailActivity", "Failed to delete employee: ${e.message}")
                Toast.makeText(requireContext(), "Xóa thất bại: ${e.message}", Toast.LENGTH_LONG).show()
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