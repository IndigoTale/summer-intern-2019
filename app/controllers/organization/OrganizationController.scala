package controllers.organization

import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, MessagesControllerComponents}

import persistence.organization.dao.OrganizationDAO
import model.site.organization.SiteViewValueOrganizationList
import model.site.organization.SiteViewValueOrganizationAdd
import model.site.organization.SiteViewValueOrganizationEdit


import persistence.facility.dao.FacilityDAO
import persistence.facility.model.Facility.formForFacilitySearch
import persistence.facility.model.Facility.formForFacilityEdit
import persistence.facility.model.Facility.formForFacilityAdd

import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO

import persistence.organization.dao.OrganizationDAO
import persistence.organization.model.Organization.formForOrganizationEdit
import persistence.organization.model.OrganizationEdit
import persistence.organization.model.Organization.formForOrganizationAdd

import model.component.util.ViewValuePageLayout


class OrganizationController @javax.inject.Inject()(
    val organizationDao: OrganizationDAO,
    val daoLocation: LocationDAO,
    val facilityDao: FacilityDAO,
    cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
    implicit lazy val executionContext = defaultExecutionContext

    def list = Action.async { implicit request =>
        for {
            organizationSeq <- organizationDao.findAll
        } yield {
            val vv = SiteViewValueOrganizationList(
                layout        = ViewValuePageLayout(id = request.uri),
                organizations = organizationSeq
            )
            Ok(views.html.site.organization.list.Main(vv))
        }
    }

    def create = Action.async { implicit request =>
        formForOrganizationAdd.bindFromRequest.fold(
            errors => {
                for {
                    organizationSeq <- organizationDao.findAll
                } yield {
                    val vv = SiteViewValueOrganizationList(
                        layout        = ViewValuePageLayout(id = request.uri),
                        organizations = organizationSeq
                    )
                    BadRequest(views.html.site.organization.list.Main(vv))
                }
            },
            form => {
                organizationDao.create(form.locationId, form.name, form.address)
                for {
                    organizationSeq <- organizationDao.findAll
                } yield {
                    val vv = SiteViewValueOrganizationList(
                        layout        = ViewValuePageLayout(id = request.uri),
                        organizations = organizationSeq
                    )
                    Ok(views.html.site.organization.list.Main(vv))
                }
            }
        )
    }

    def add = Action.async { implicit request =>
        for {
            locSeq <- daoLocation.getCities()
        } yield {
            val vv = SiteViewValueOrganizationAdd(
                layout = ViewValuePageLayout(id = request.uri),
                location = locSeq
            )
            Ok(views.html.site.organization.add.Main(vv, formForOrganizationAdd))
        }
    }

    def edit(id: Long) = Action { implicit request =>
        formForOrganizationEdit.bindFromRequest.fold(
            errors => {
                Redirect(routes.OrganizationController.show(id))
            },
            form => {
                organizationDao.update(id, form.locationId, form.name, form.address)                
                Redirect(routes.OrganizationController.list)
            }
        )
    }

    def show(id: Long) = Action.async { implicit request =>
        for {
            organization <- organizationDao.get(id)
            locSeq <- daoLocation.getCities()
        } yield {
            val header = SiteViewValueOrganizationEdit(
                layout = ViewValuePageLayout(id = request.uri),
                organization = organization,
                location = locSeq
            )
            val filledformForOrganizationEdit = formForOrganizationEdit.fill(OrganizationEdit(
                organization.get.locationId,
                organization.get.name,
                organization.get.address
            ))
            Ok(views.html.site.organization.edit.Main(header, filledformForOrganizationEdit))
        }
    }

    def delete(id: Long) = Action {
        organizationDao.delete(id)
        Redirect(routes.OrganizationController.list)
    }
    
}
