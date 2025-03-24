package com.example.tlucontact

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Nếu chưa đăng nhập, chuyển về LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        try {
            setContentView(R.layout.activity_main)
            Log.d("MainActivity", "setContentView completed")

            val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
            Log.d("MainActivity", "bottomNavView initialized")

            if (savedInstanceState == null) {
                loadFragment(DepartmentFragment())
                Log.d("MainActivity", "Loaded DepartmentFragment initially")
            }

            bottomNavView.setOnItemSelectedListener { menuItem ->
                Log.d("MainActivity", "BottomNav item selected: ${menuItem.itemId}")
                when (menuItem.itemId) {
                    R.id.menu_item_department -> {
                        loadFragment(DepartmentFragment())
                        true
                    }
                    R.id.menu_item_employee -> {
                        loadFragment(EmployeeFragment())
                        true
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        Log.d("MainActivity", "Loading fragment: ${fragment.javaClass.simpleName}")
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            Log.d("MainActivity", "Fragment loaded successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading fragment: ${e.message}", e)
        }
    }
}