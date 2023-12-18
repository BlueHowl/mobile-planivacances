package be.helmo.planivacances.view.fragments.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.presenter.viewmodel.ActivityListItemVM

class CalendarAdapter(val clickListener: (String) -> Unit) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    val activitiesList: MutableList<ActivityListItemVM> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.calendar_activity_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return activitiesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val activityItemVM: ActivityListItemVM = activitiesList[position]
        holder.bind(activityItemVM,clickListener)
    }

    fun clearActivitiesList() {
        activitiesList.clear()
        notifyDataSetChanged()
    }

    fun addActivity(activity: ActivityListItemVM) {
        activitiesList.add(activity)
        notifyItemInserted(activitiesList.size - 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityContainer : ConstraintLayout = itemView.findViewById(R.id.clActivityCalendarItem)
        val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val activityDate: TextView = itemView.findViewById(R.id.tvActivityTime)

        fun bind(activityItemVM: ActivityListItemVM, clickListener: (String) -> Unit) {
            activityName.text = activityItemVM.title
            activityDate.text = String.format("Début : %s\nDurée : %s",activityItemVM.startHour,activityItemVM.duration)
            activityContainer.setOnClickListener {clickListener(activityItemVM.aid)}
        }
    }

}