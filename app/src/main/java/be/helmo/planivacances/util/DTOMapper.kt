package be.helmo.planivacances.util

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.presenter.viewmodel.*
import be.helmo.planivacances.service.dto.ActivityDTO
import be.helmo.planivacances.service.dto.GroupDTO
import be.helmo.planivacances.service.dto.GroupInviteDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import com.google.android.gms.maps.model.LatLng

object DTOMapper {

    fun groupToGroupDTO(group: Group) : GroupDTO {
        return GroupDTO(
            "null",
            group.groupName,
            group.description,
            group.startDate,
            group.endDate,
            placeToPlaceDTO(group.place)
        )
    }

    fun groupDtoToGroup(groupDTO: GroupDTO) : Group {
        return Group(
            groupDTO.groupName,
            groupDTO.description,
            groupDTO.startDate,
            groupDTO.endDate,
            placeDtoToPlace(groupDTO.place),
            groupDTO.owner!!
        )
    }

    fun groupToGroupListItem(gid: String, group: Group) : GroupListItemVM {
        return GroupListItemVM(
            gid,
            group.groupName,
            group.description,
            group.startDate,
            group.endDate
        )
    }

    fun groupToGroupVM(group: Group) : GroupVM {
        val placeVM : PlaceVM = placeToPlaceVM(group.place)
        return GroupVM(group.groupName,group.description,group.startDate,group.endDate,placeVM)
    }

    fun groupVMToGroupDTO(groupVM: GroupVM,owner: String,gid:String) : GroupDTO {
        val placeDTO = placeVMToPlaceDTO(groupVM.place)
        return GroupDTO(gid,groupVM.name,groupVM.description,groupVM.startDate,groupVM.endDate,placeDTO,owner)
    }

    fun placeToPlaceDTO(place: Place) : PlaceDTO {
         return PlaceDTO(
            place.country,
            place.city,
            place.street,
            place.number,
            place.postalCode,
            place.latLng.latitude,
            place.latLng.longitude
        )
    }

    fun placeDtoToPlace(placeDTO: PlaceDTO) : Place {
        return Place(
            placeDTO.country,
            placeDTO.city,
            if (placeDTO.street != null) placeDTO.street else "",
            if (placeDTO.number != null) placeDTO.number else "",
            placeDTO.postalCode,
            LatLng(placeDTO.lat, placeDTO.lon)
        )
    }

    fun placeToPlaceVM(place: Place) : PlaceVM {
        return PlaceVM(place.street,place.number,place.postalCode,place.city,place.country,
            LatLng(place.latLng.latitude,place.latLng.longitude)
        )
    }

    fun placeVMToPlaceDTO(placeVM: PlaceVM) : PlaceDTO {
        return PlaceDTO(placeVM.country,placeVM.city,placeVM.street,placeVM.number,placeVM.postalCode,placeVM.latLng.latitude,placeVM.latLng.longitude)
    }

    fun placeDTOToPlaceVM(placeDTO: PlaceDTO) : PlaceVM {
        return PlaceVM(placeDTO.street,placeDTO.number,placeDTO.postalCode,placeDTO.city,placeDTO.country,
            LatLng(placeDTO.lat,placeDTO.lon)
        )
    }
    fun activityVMToActivityDTO(activityVM: ActivityVM) : ActivityDTO {
        val placeDTO : PlaceDTO = placeVMToPlaceDTO(activityVM.place)
        return ActivityDTO(activityVM.title,activityVM.description,activityVM.startDate,activityVM.duration,placeDTO)
    }

    fun activityDTOToActivityVM(activityDTO: ActivityDTO) : ActivityVM {
        val placeVM : PlaceVM = placeDTOToPlaceVM(activityDTO.place)
        return ActivityVM(activityDTO.title,activityDTO.description,activityDTO.startDate,activityDTO.duration,placeVM)
    }

    fun groupInviteDTOToGroupInvitationVM(groupInviteDTO: GroupInviteDTO) : GroupInvitationVM {
        return GroupInvitationVM(groupInviteDTO.gid,groupInviteDTO.groupName,"Rejoindre le groupe")
    }
}