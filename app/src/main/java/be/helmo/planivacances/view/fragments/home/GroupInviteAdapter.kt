package be.helmo.planivacances.view.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.presenter.viewmodel.GroupInvitationVM

class GroupInviteAdapter(val clickListener: (String,Boolean) -> Unit) : RecyclerView.Adapter<GroupInviteAdapter.ViewHolder>() {
    val groupInvitationsList: MutableList<GroupInvitationVM> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.group_invite_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groupInvitationsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupInvitationVM: GroupInvitationVM = groupInvitationsList[position]
        holder.bind(groupInvitationVM,clickListener)
    }

    fun clearInvitationsList() {
        groupInvitationsList.clear()
        notifyDataSetChanged()
    }

    fun addGroupInvitation(groupInvitation: GroupInvitationVM) {
        groupInvitationsList.add(groupInvitation)
        notifyItemInserted(groupInvitationsList.size - 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.tvGroupInviteTitle)
        val content: TextView = itemView.findViewById(R.id.tvGroupInviteText)
        val acceptBtn : ImageButton = itemView.findViewById(R.id.ibAcceptGroup)
        val declineBtn : ImageButton = itemView.findViewById(R.id.ibDeclineGroup)

        fun bind(groupInvitationVM: GroupInvitationVM, clickListener: (String,Boolean) -> Unit) {
            groupName.text = groupInvitationVM.groupName
            content.text = groupInvitationVM.content
            acceptBtn.setOnClickListener {clickListener(groupInvitationVM.gid,true)}
            declineBtn.setOnClickListener {clickListener(groupInvitationVM.gid,false)}
        }
    }

}