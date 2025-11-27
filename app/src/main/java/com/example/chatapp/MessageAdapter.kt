// File: MessageAdapter.kt
package com.example.chatapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(
    private val context: Context,
    private val messageList: ArrayList<Message>,
    private val senderRoom: String,        // ← ADD THIS
    private val receiverRoom: String       // ← ADD THIS
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder is SentViewHolder) {
            holder.sentMessage.text = currentMessage.message ?: ""

            holder.itemView.setOnLongClickListener {
                showEditDeleteDialog(currentMessage)
                true
            }
        } else if (holder is ReceiveViewHolder) {
            holder.receiveMessage.text = currentMessage.message ?: ""
            holder.itemView.setOnLongClickListener(null)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount() = messageList.size

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
    }

    private fun showEditDeleteDialog(message: Message) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(context)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editMessage(message)
                    1 -> deleteMessage(message)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun editMessage(message: Message) {
        val editText = EditText(context).apply {
            setText(message.message)
            setSelection(text.length)
        }

        AlertDialog.Builder(context)
            .setTitle("Edit Message")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty() && newText != message.message) {
                    updateMessage(message.messageId!!, newText)
                    message.message = newText
                    notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMessage(message: Message) {
        AlertDialog.Builder(context)
            .setTitle("Delete Message")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                val messageId = message.messageId!!
                deleteFromFirebase(senderRoom, messageId)
                deleteFromFirebase(receiverRoom, messageId)

                messageList.remove(message)
                notifyDataSetChanged()
                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateMessage(messageId: String, newText: String) {
        val updates = mapOf("message" to newText)

        FirebaseDatabase.getInstance().reference
            .child("chats").child(senderRoom).child("messages").child(messageId)
            .updateChildren(updates)

        FirebaseDatabase.getInstance().reference
            .child("chats").child(receiverRoom).child("messages").child(messageId)
            .updateChildren(updates)
    }

    private fun deleteFromFirebase(roomId: String, messageId: String) {
        FirebaseDatabase.getInstance().reference
            .child("chats").child(roomId).child("messages").child(messageId)
            .removeValue()
    }
}