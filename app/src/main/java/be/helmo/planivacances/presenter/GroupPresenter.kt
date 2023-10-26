package be.helmo.planivacances.presenter

import be.helmo.planivacances.factory.interfaces.ICreateGroupCallback
import be.helmo.planivacances.view.interfaces.IGroupPresenter

class GroupPresenter : IGroupPresenter {

    var cgcb: ICreateGroupCallback? = null

    override fun createGroupClick() {
        cgcb?.showCreateGroup()
    }

    override fun setCreateGroupCallback(createGroupCallback: ICreateGroupCallback?) {
        this.cgcb = createGroupCallback
    }

    override fun createGroup() {

    }
}