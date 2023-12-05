package be.helmo.planivacances.util

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.GroupListItem
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.service.dto.GroupDTO
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

    fun groupToGroupListItem(gid: String, group: Group) : GroupListItem {
        return GroupListItem(
            gid,
            group.groupName,
            group.description,
            group.startDate,
            group.endDate
        )
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
}