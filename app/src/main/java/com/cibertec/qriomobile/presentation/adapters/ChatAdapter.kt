package com.cibertec.qriomobile.presentation.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.R

data class ChatMessage(val text: String, val isUser: Boolean)

class ChatAdapter(private val messages: MutableList<ChatMessage> = mutableListOf()) : 
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }

    fun removeMessageAt(index: Int) {
        if (index in 0 until messages.size) {
            messages.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.layoutMessage)
        val txtSender: TextView = view.findViewById(R.id.txtSender)
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)

        fun bind(msg: ChatMessage) {
            txtMessage.text = msg.text

            if (msg.isUser) {
                txtSender.text = "Tú"
                layout.gravity = Gravity.END
                txtSender.gravity = Gravity.END
                // Color suave para el usuario (amarillo claro/dorado suave)
                txtMessage.setBackgroundColor(0xFFFFF8E1.toInt()) 
            } else {
                txtSender.text = "IA Qrio"
                layout.gravity = Gravity.START
                txtSender.gravity = Gravity.START
                // Gris claro para la IA
                txtMessage.setBackgroundColor(0xFFEEEEEE.toInt())
            }
        }
    }
}