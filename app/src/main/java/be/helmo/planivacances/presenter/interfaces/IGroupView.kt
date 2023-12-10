package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.GroupDetailVM

interface IGroupView : IShowToast {
    fun buildItinerary(latitude:String,longitude:String)
    fun setGroupInfos(group: GroupDetailVM)
    fun onGroupDeleted()
}