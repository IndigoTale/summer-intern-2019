package model.site.facility

import model.component.util.ViewValuePageLayout
import persistence.facility.model.Facility
import persistence.geo.model.Location

// 表示: 施設一覧
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueFacilityAdd(
  layout:   ViewValuePageLayout,
  location: Seq[Location],
)