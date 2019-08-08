package persistence.organization.model

import play.api.data._
import play.api.data.Forms._
import java.time.LocalDateTime
import persistence.geo.model.Location

case class Organization(
    id:          Option[Organization.Id],    
    locationId:  Location.Id,                        // 地域ID
    name:        String,                             // 施設名
    address:     String,                             // 住所(詳細)
    updatedAt:   LocalDateTime = LocalDateTime.now,  // データ更新日
    createdAt:   LocalDateTime = LocalDateTime.now   // データ作成日
)

case class OrganizationEdit(
    locationId: Location.Id,
    name: String,
    address: String,    
)

case class OrganizationAdd(
    locationId: Location.Id,
    name: String,
    address: String,
)

object Organization {
    type Id = Long

    val formForOrganizationEdit = Form(
        mapping(
            "locationId"  -> text,
            "name"        -> nonEmptyText,
            "address"     -> nonEmptyText,
        )(OrganizationEdit.apply)(OrganizationEdit.unapply)
    )
    val formForOrganizationAdd = Form(
        mapping(
            "locationId" -> text,
             "name" -> text,
            "address" -> text,
        )(OrganizationAdd.apply)(OrganizationAdd.unapply)
    )
}