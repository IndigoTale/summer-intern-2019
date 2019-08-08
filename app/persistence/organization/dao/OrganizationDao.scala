package persistence.organization.dao

import java.time.LocalDateTime
import scala.concurrent.Future

import slick.jdbc.JdbcProfile

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider

import persistence.organization.model.Organization
import persistence.geo.model.Location

class OrganizationDAO @javax.inject.Inject()(
    val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
    import profile.api._

    lazy val slick = TableQuery[OrganizationTable]

    def findAll: Future[Seq[Organization]] =
        db.run {
          slick.result
        }

    def get(id: Organization.Id): Future[Option[Organization]] = 
        db.run {
            slick
                .filter(_.id === id)
                .result.headOption
        }

    def update(id: Long, locationId: Location.Id, name: String, address: String): Unit = 
        db.run {
            slick
                .filter(_.id === id)
                .map(p => (p.locationId, p.name, p.address))
                .update((locationId, name, address))
        }
    
    def create(locationId: Location.Id, name:String, address:String): Unit = 
        db.run {
            slick
                .map(p => (p.locationId, p.name, p.address)) += ((locationId, name, address))
        }
    
    def delete(id: Long): Unit = 
        db.run {
            slick
                .filter(_.id === id)
                .delete
        }

    class OrganizationTable(tag: Tag) extends Table[Organization](tag, "organization") {
        def id            = column[Organization.Id] ("id", O.PrimaryKey, O.AutoInc)
        def locationId    = column[Location.Id] ("location_id")
        def name          = column[String] ("name")
        def address       = column[String] ("address")
        def updatedAt     = column[LocalDateTime]  ("updated_at")
        def createdAt     = column[LocalDateTime]  ("created_at")

        def * = (
            id.?,
            locationId,
            name,
            address,
            updatedAt,
            createdAt
        ) <> (
            (Organization.apply _).tupled,
            (v: TableElementType) => Organization.unapply(v).map(_.copy(_5 = LocalDateTime.now))
        )
    }
}