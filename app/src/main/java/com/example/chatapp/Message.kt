// File: Message.kt
package com.example.chatapp

class Message {
    var message: String? = null
    var senderId: String? = null
    var messageId: String? = null   // ← ADD THIS

    constructor()
    constructor(message: String?, senderId: String?) {
        this.message = message
        this.senderId = senderId
    }

    // Add this constructor for edit/delete
    constructor(message: String?, senderId: String?, messageId: String?) {
        this.message = message
        this.senderId = senderId
        this.messageId = messageId
    }
}