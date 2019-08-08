/*
 * This file is part of Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package controllers.facility

import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, MessagesControllerComponents}
// persistence: 永続化
import persistence.facility.model.Facility
import persistence.facility.dao.FacilityDAO
import persistence.facility.model.Facility.formForFacilitySearch
import persistence.facility.model.Facility.formForFacilityEdit
import persistence.facility.model.Facility.formForFacilityAdd
import persistence.facility.model.FacilityEdit

import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO
// model
import model.site.facility.SiteViewValueFacilityList
import model.site.facility.SiteViewValueFacilityShow
import model.site.facility.SiteViewValueFacilityEdit
import model.site.facility.SiteViewValueFacilityAdd

import model.component.util.ViewValuePageLayout


// 施設
//~~~~~~~~~~~~~~~~~~~~~
class FacilityController @javax.inject.Inject()(
  // val: immutable, var: mutable
  val facilityDao: FacilityDAO,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

  /**
    * 施設一覧ページ
    */
  def list = Action.async { implicit request =>
    // implicit: 暗黙の型変換
    // https://dwango.github.io/scala_text/implicit.html
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      // daoLocation = LocationDAO
      // app/persistence/geo/dao/LocationDao.scala
      // filterByIds: LocationIDを複数個入力に受け、地域情報を取得する
      facilitySeq <- facilityDao.findAll
      // facilityDao = FacilityDAO
      // app/persistence/facility/dao/FacilityDao.scala
    } yield {
      // app/model/site/facility/FacilityList.scala
      val vv = SiteViewValueFacilityList(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facilities = facilitySeq
      )
      Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch))
      // Ok: 200
      // BadRequest: 400
      // Forbidden: 403
      // NotFound: 404
      // Redirect
    }
    // for { A } yield { B }: AしてからBする
  }

  /**
   * 施設検索
   */
  def search = Action.async { implicit request =>
    formForFacilitySearch.bindFromRequest.fold(
      errors => {
       for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- facilityDao.findAll
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          BadRequest(views.html.site.facility.list.Main(vv, errors))
        }
      },
      form   => {
        for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- form.locationIdOpt match {
            case Some(id) =>
              for {
                locations   <- daoLocation.filterByPrefId(id)
                facilitySeq <- facilityDao.filterByLocationIds(locations.map(_.id))
              } yield facilitySeq
            case None     => facilityDao.findAll
          }
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch.fill(form)))
        }
      }
    )
  }

  def create = Action.async { implicit request =>
    formForFacilityAdd.bindFromRequest.fold(
      errors => {
        for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- facilityDao.findAll
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          BadRequest(views.html.site.facility.list.Main(vv, formForFacilitySearch))
        }
      },
      form => {
        facilityDao.create(form.locationId, form.name, form.address, form.description)
        for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- facilityDao.findAll
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch))
        }
      }
    )
  }

  def add = Action.async { implicit request => 
    for {
      locSeq <- daoLocation.getCities()
    } yield {
      val vv = SiteViewValueFacilityAdd(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq
      )
      Ok(views.html.site.facility.add.Main(vv, formForFacilityAdd))
    }
  }

  /**
    * 個々の施設の編集ページ
    */
  def edit(id: Long) = Action { implicit request =>
    formForFacilityEdit.bindFromRequest.fold(
      errors => {
        // list関数と同じ        
        Redirect(routes.FacilityController.show(id))          
        
      },
      form => {
        facilityDao.update(id, form.name, form.address, form.description)  
        Redirect(routes.FacilityController.list)      
      }
    )
  }

  /**
    * 個々の施設ページ
    */
  def show(id: Long) = Action.async { implicit request =>
    for {
        facility <- facilityDao.get(id)
      } yield {
        val header = SiteViewValueFacilityEdit(
          layout = ViewValuePageLayout(id = request.uri),
          facility = facility
        )
        val filledformForFacilityEdit = formForFacilityEdit.fill(FacilityEdit(
          facility.get.name,
          facility.get.address,
          facility.get.description
        ))
        Ok(views.html.site.facility.edit.Main(header, filledformForFacilityEdit))
    }
  }  


  def delete(id: Long) = Action {
    facilityDao.delete(id)
    Redirect(routes.FacilityController.list)
  }
}
