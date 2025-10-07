package com.sampath.finora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var btnOffline: MaterialButton
    private lateinit var createAccount: TextView
    private lateinit var forgotPassword: TextView

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnOffline = findViewById(R.id.btnOffline)
        createAccount = findViewById(R.id.createAccount)
        forgotPassword = findViewById(R.id.forgot)

        btnSignIn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                return@setOnClickListener
            } else emailLayout.error = null

            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                return@setOnClickListener
            } else passwordLayout.error = null

            if (dbHelper.validateUser(email, password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // TODO: You might want to save the user ID here (e.g., SharedPreferences)
                startActivity(Intent(this, HomeDashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        btnOffline.setOnClickListener {
            Toast.makeText(this, "Continue Offline", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeDashboardActivity::class.java))
            finish()
        }

        createAccount.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            finish()
        }

        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
            // Implement reset password logic here
        }
    }
}