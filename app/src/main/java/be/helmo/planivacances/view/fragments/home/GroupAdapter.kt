package be.helmo.planivacances.view.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.domain.GroupListItem
import java.text.SimpleDateFormat

class GroupAdapter(val context: Context,
                   val groups: List<GroupListItem>,
                   val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.group_item, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]

        val formatter = SimpleDateFormat(context.getString(R.string.date_short_format))
        val startDate = formatter.format(group.startDate)
        val endDate = formatter.format(group.endDate)

        holder.tvName.text = group.groupName
        holder.tvDescription.text = group.description
        holder.tvPeriod.text = "$startDate - $endDate"

        // Set click listener to handle item clicks
        holder.itemView.setOnClickListener { clickListener(group.gid!!) }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvGroupItemName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvGroupItemDescription)
        val tvPeriod: TextView = itemView.findViewById(R.id.tvGroupItemPeriod)
    }

}
