package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.GroupInvitationVM
import be.helmo.planivacances.presenter.viewmodel.GroupListItemVM

interface IHomeView : IShowToast {
    fun setGroupList(groups: List<GroupListItemVM>)
    fun onGroupInvitationsLoaded(invitations: List<GroupInvitationVM>)

    fun onGroupInvitationAccepted()

    fun onGroupInvitationDeclined()
}