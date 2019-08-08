package model.site.facility

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.facility.model.Facility

// 表示: 施設一覧
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueFacilityEdit(
    layout:   ViewValuePageLayout,
    location: Seq[Location],
    facility: Option[Facility]
)