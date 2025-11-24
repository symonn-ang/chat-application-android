package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLog: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progBar: ProgressBar
    private lateinit var regButton: Button

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish() // optional, back or none ;3
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.username)
        editTextPassword = findViewById(R.id.password)
        buttonLog = findViewById(R.id.loginBtn)
        progBar = findViewById(R.id.progressBar)
        regButton = findViewById(R.id.registerBtn)
        regButton.setOnClickListener {

            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish() // optional, back or none ;3
        }

        buttonLog.setOnClickListener {
            progBar.setVisibility(View.VISIBLE);
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
                progBar.setVisibility(View.GONE);
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                progBar.setVisibility(View.GONE);
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progBar.setVisibility(View.GONE); // don't forget
                    if (task.isSuccessful) {
                        // sign in success
                        Log.d("LoginActivity", "signInWithEmail:success")
                        val user = auth.currentUser
//                        updateUI(user) custom apparently maybe touch later?
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // sign in fails
                        Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this, // 'this' refers to the Activity context
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
//                        updateUI(null)
                    }
                }
        }

        // bottom

    }
}