package model.site.facility

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.facility.model.Facility

// 表示: 施設追加
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueFacilityAdd(
  layout:   ViewValuePageLayout,
  location: Seq[Location],
)