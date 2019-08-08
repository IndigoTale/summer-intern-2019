/*
 * This file is part of Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package controllers.facility

import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import persistence.facility.dao.FacilityDAO
import persistence.facility.model.Facility.formForFacilitySearch
import persistence.facility.model.Facility.formForFacilityEdit
import persistence.facility.model.Facility.formForFacilityAdd
import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO
import model.site.facility.SiteViewValueFacilityList
import model.site.facility.SiteViewValueFacilityEdit
import model.site.facility.SiteViewValueFacilityAdd
import model.component.util.ViewValuePageLayout


// 施設
//~~~~~~~~~~~~~~~~~~~~~
class FacilityController @javax.inject.Inject()(
  val facilityDao: FacilityDAO,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

  /**
    * 施設一覧ページ
    */
  def list = Action.async { implicit request =>
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
  /** 
    * 施設詳細ページ
    */
  def show(id:Long) = Action.async { implicit request =>
    for {
        locSeq   <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
        facilitySeq <- facilityDao.get(id)
      } yield {
          val vv = SiteViewValueFacilityEdit(
            layout = ViewValuePageLayout(id = request.uri),
            location = locSeq,
            facility = facilitySeq
        )
        Ok(views.html.site.facility.edit.Main(vv, formForFacilityEdit))
    }
  }
  /**
    * 施設編集ページ
    */
  def edit(id: Long) = Action.async { implicit request =>
    for {
        locSeq   <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
        facilitySeq <- facilityDao.get(id)
      } yield {
        
        val vv = SiteViewValueFacilityEdit(
          layout = ViewValuePageLayout(id = request.uri),
          location = locSeq,
          facility = facilitySeq
        )
        Ok(views.html.site.facility.edit.Main(vv, formForFacilityEdit))
    }
  }
  def add() = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
    } yield {
        val vv = SiteViewValueFacilityAdd(
          layout = ViewValuePageLayout(id = request.uri),
          location = locSeq
        )
        Ok(views.html.site.facility.add.Main(vv,formForFacilityAdd))
    }
  }
  def create() = Action.async { implicit request =>
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
        facilityDao.create(form)
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
  def update(id: Long) = Action.async { implicit request =>
    formForFacilityEdit.bindFromRequest.fold(
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
          Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch))
        }
      },
      form => {
        facilityDao.update(id, form)
        for {
          facility <- facilityDao.get(id)
          locSeq   <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
        } yield {
          val vv = SiteViewValueFacilityEdit(
            layout = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facility = facility
          )
          Ok(views.html.site.facility.edit.Main(vv, formForFacilityEdit))
        }
      }
    )
  }
  def getSubLocations(prefId : String) = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByPrefId(prefId)
    } yield {

      var gsls = List[String]()
      println(locSeq.length-1)
      for (i <- 1 to locSeq.length-1){
        if (locSeq(i).nameCity != None) {
          gsls = gsls :+ locSeq(i).nameCity.get
        } else if(locSeq(i).nameWard != None){
          gsls = gsls :+ locSeq(i).nameWard.get
        } else {
          gsls = gsls :+ locSeq(i).nameCounty.get
        }
      }
      val jsonObject:JsValue = Json.toJson(gsls.distinct)
      Ok(jsonObject)
    }
  }
  /**
    * 施設作成ページ
    */
  // def create = Action.async  { implicit request =>
  //   for {
  //     locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
  //     facilitySeq <- facilityDao.findAll
  //   } yield {
  //     val vv = SiteViewValueFacilityList(
  //       layout     = ViewValuePageLayout(id = request.uri),
  //       location   = locSeq,
  //       facilities = facilitySeq
  //     )
  //     Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch))
  //   }
  // }
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
}
