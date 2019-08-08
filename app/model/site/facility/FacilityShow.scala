package model.site.facility

import model.component.util.ViewValuePageLayout
import persistence.facility.model.Facility

// 表示: 施設一覧
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueFacilityShow(
  layout:   ViewValuePageLayout,
  facility: Facility
)