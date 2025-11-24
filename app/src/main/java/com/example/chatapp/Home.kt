package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Home : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var logoutButton: Button // lateinit when you dont want to null stuf and delay the assignment
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.user)
        logoutButton = findViewById(R.id.logoutBtn)
//        auth.currentUser?.let {
//            user = it
//        } ?: run {
//            // Handle not logged in state
//            Log.e("Auth", "No user logged in")
//        }
//          check later
        user = auth.currentUser // current
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            textView.text = user?.email
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}