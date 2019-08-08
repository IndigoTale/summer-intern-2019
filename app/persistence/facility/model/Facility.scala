/*
 * This file is part of the Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package persistence.facility.model

import play.api.data._
import play.api.data.Forms._
import java.time.LocalDateTime
import persistence.geo.model.Location
import persistence.organization.model.Organization

// 施設情報 (sample)
//~~~~~~~~~~~~~
case class Facility(
  id:          Option[Facility.Id],                // 施設ID
  locationId:  Location.Id,                        // 地域ID
  name:        String,                             // 施設名
  address:     String,                             // 住所(詳細)
  description: String,                             // 施設説明  
  updatedAt:   LocalDateTime = LocalDateTime.now,  // データ更新日
  createdAt:   LocalDateTime = LocalDateTime.now,  // データ作成日
  organizationId: Option[Organization.Id],         // 組織ID
)

// 施設検索
case class FacilitySearch(
  locationIdOpt: Option[Location.Id]
)

case class FacilityEdit(
  name: String,
  address: String,
  description: String,
)

case class FacilityAdd(
  locationId: Location.Id,
  name: String,
  address: String,
  description: String,
)

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~~~
object Facility {

  // --[ 管理ID ]---------------------------------------------------------------
  type Id = Long

  // --[ フォーム定義 ]---------------------------------------------------------
  // https://www.playframework.com/documentation/ja/2.4.x/ScalaForms
  val formForFacilitySearch = Form(
    mapping(
      "locationId" -> optional(text),
    )(FacilitySearch.apply)(FacilitySearch.unapply)
  )
  val formForFacilityEdit = Form(
    mapping(
      "name"        -> nonEmptyText,
      "address"     -> nonEmptyText,
      "description" -> text,
    )(FacilityEdit.apply)(FacilityEdit.unapply)
  )
  val formForFacilityAdd = Form(
    mapping(
      "locationId" -> text,
      "name" -> text,
      "address" -> text,
      "description" -> text
    )(FacilityAdd.apply)(FacilityAdd.unapply)
  )
}

