package be.helmo.planivacances.view.fragments.tchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.service.dto.MessageDTO
import be.helmo.planivacances.util.DateFormatter

class TchatAdapter() :
    RecyclerView.Adapter<TchatAdapter.ViewHolder>() {
    private val messagesList: MutableList<MessageDTO> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResId = if (viewType == VIEW_TYPE_ME) {
            R.layout.message_item_sending
        } else {
            R.layout.message_item_receiving
        }
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(layoutResId, parent, false)
        return ViewHolder(view,viewType)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: MessageDTO = messagesList[position]
        holder.bind(message,getItemViewType(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList[position].sender == "me") VIEW_TYPE_ME else VIEW_TYPE_OTHER
    }

    fun addMessage(message: MessageDTO) {
        if (messagesList.size == 100) {
            messagesList.removeAt(0)
        }
        messagesList.add(message)
        notifyItemInserted(messagesList.size - 1)
    }

    companion object {
        const val VIEW_TYPE_ME = 0
        const val VIEW_TYPE_OTHER = 1
    }

    class ViewHolder(itemView: View,viewType: Int) : RecyclerView.ViewHolder(itemView) {
        private var displayName: TextView? = null
        private var messageText: TextView
        private var messageTime: TextView

        init {
            if (viewType == VIEW_TYPE_ME) {
                messageText = itemView.findViewById(R.id.textMessageS)
                messageTime = itemView.findViewById(R.id.messageTimeS)
            } else {
                displayName = itemView.findViewById(R.id.senderName)
                messageText = itemView.findViewById(R.id.textMessageR)
                messageTime = itemView.findViewById(R.id.messageTimeR)
            }
        }

        fun bind(message: MessageDTO,viewType: Int) {
            if (viewType == VIEW_TYPE_OTHER) {
                displayName?.text = message.displayName
            }
                messageText?.text = message.content
                messageTime?.text = DateFormatter.formatTimestampForDisplay(message.time)
            }
        }
}