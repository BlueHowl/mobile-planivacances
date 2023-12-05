package be.helmo.planivacances.presenter.interfaces

interface IGroupView : IShowToast {
    fun buildItinerary(latitude:String,longitude:String)
}