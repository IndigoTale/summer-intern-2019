package model.site.organization

import model.component.util.ViewValuePageLayout
import persistence.organization.model.Organization
import persistence.geo.model.Location

// 表示: 施設一覧
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueOrganizationEdit(
  layout:   ViewValuePageLayout,
  location: Seq[Location],
  organization: Option[Organization]
)