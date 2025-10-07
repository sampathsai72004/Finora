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

class Signup : AppCompatActivity() {

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var btnSignUp: MaterialButton
    private lateinit var signInText: TextView

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        // Initialize views
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout)
        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        confirmPasswordInput = findViewById(R.id.confirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        signInText = findViewById(R.id.signInText)

        // Sign Up button click
        btnSignUp.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                return@setOnClickListener
            } else emailLayout.error = null

            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                return@setOnClickListener
            } else passwordLayout.error = null

            if (confirmPassword.isEmpty()) {
                confirmPasswordLayout.error = "Confirm your password"
                return@setOnClickListener
            } else confirmPasswordLayout.error = null

            if (password != confirmPassword) {
                confirmPasswordLayout.error = "Passwords do not match"
                return@setOnClickListener
            } else confirmPasswordLayout.error = null

            if (dbHelper.isUserExists(email)) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate default name from email and capitalize it
            val defaultName = capitalizeWords(email.substringBefore('@').replace('.', ' '))

            val inserted = dbHelper.insertUser(email, password, defaultName)
            if (inserted) {
                Toast.makeText(this, "Signup successful. Logged in as $defaultName", Toast.LENGTH_SHORT).show()

                // --- CRITICAL: PASS USER ID TO DASHBOARD ---
                // You must get the newly inserted user's ID to use the dashboard
                // For simplicity, we'll try to get the ID immediately
                val newUserId = dbHelper.getUserId(email)

                val intent = Intent(this, HomeDashboardActivity::class.java)
                intent.putExtra("USER_ID", newUserId) // Pass the ID
                startActivity(intent)

                finish()
            } else {
                Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to login
        signInText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    companion object {
        /**
         * Helper function to capitalize words (moved into Companion Object for cleanliness).
         */
        fun capitalizeWords(input: String): String =
                input.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
}