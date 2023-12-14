package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.GroupVM

interface IUpdateGroupView : IShowToast {
    fun onGroupUpdated()
    fun setCurrentGroup(groupVM: GroupVM)
}