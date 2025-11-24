package com.example.chatapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.ProgressBar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registration : AppCompatActivity() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progBar: ProgressBar
    private lateinit var backButton: Button
    private lateinit var mDbRef: DatabaseReference

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // optional, back or none ;3
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.name)
        editTextEmail = findViewById(R.id.username)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.registerBtn)
        progBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            // Create an Intent to go back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // optional: closes the current activity so user can't press back to return here
        }

        buttonReg.setOnClickListener {
            progBar.setVisibility(View.VISIBLE);
            val name = editTextName.text.toString().trim()
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

            signUp(name, email, password)

        }


    } // end of onCreate
    private fun signUp(name:String, email:String, password:String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progBar.setVisibility(View.GONE);
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("Registration", "createUserWithEmail:success")
                    addUserToDatabase(name, email, auth.currentUser?.uid!!)
                    val user: FirebaseUser? = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // optional, back or none ;3
//                        updateUI(user)
                } else {
                    // Sign in fails
                    Log.w("Registration", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                }
            }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))

    }


}