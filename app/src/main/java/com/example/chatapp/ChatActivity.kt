package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name
        mDbRef = FirebaseDatabase.getInstance().reference

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList, senderRoom!!, receiverRoom!!)
        // ↑↑↑ Pass senderRoom & receiverRoom

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // Load messages
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.messageId = postSnapshot.key  // ← CRITICAL: Save messageId!
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        // Send message
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isEmpty()) return@setOnClickListener

            // GENERATE SAME KEY ONCE
            val newMessageKey = mDbRef.child("chats").child(senderRoom!!).child("messages").push().key!!

            val messageObject = Message(messageText, senderUid, newMessageKey)  // ← Pass messageId

            // Save to BOTH rooms using THE SAME KEY
            mDbRef.child("chats").child(senderRoom!!).child("messages").child(newMessageKey)
                .setValue(messageObject)

            mDbRef.child("chats").child(receiverRoom!!).child("messages").child(newMessageKey)
                .setValue(messageObject)

            messageBox.setText("")
        }

        findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }
}