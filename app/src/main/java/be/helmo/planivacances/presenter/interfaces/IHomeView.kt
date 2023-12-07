package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.GroupListItemVM

interface IHomeView : IShowToast {

    fun setGroupList(groups: List<GroupListItemVM>)

}