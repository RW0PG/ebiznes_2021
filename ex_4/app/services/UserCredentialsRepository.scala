package services

import models.UserCredentials
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserCredentialsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val UserRepository: UserRepository)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  import UserRepository.UserTable
  val user = TableQuery[UserTable]

  class UserCredentialsTable(tag: Tag) extends Table[UserCredentials](tag, "user_credentials") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userID: Rep[Long] = column[Long]("user_ID")
    def userID_fk = foreignKey("user_fk", userID, user)(_.id)
    def firstName: Rep[String] = column[String]("firstname")
    def lastName: Rep[String] = column[String]("lastname")
    def street: Rep[String] = column[String]("street")
    def zipcode: Rep[String] = column[String]("zipcode")
    def city: Rep[String] = column[String]("city")
    def country: Rep[String] = column[String]("country")

    def * = (id, userID, firstName, lastName, street, zipcode, city, country) <> ((UserCredentials.apply _).tupled, UserCredentials.unapply)
  }

  val userCredentials = TableQuery[UserCredentialsTable]

  def create(userID: Long, firstName: String, lastName: String, street: String, zipcode: String, city: String, country: String): Future[UserCredentials] = db.run {
    (userCredentials.map(a => (a.userID, a.firstName, a.lastName, a.street, a.zipcode, a.city, a.country))
      returning userCredentials.map(_.id)
      into { case ((userID, firstName, lastName, street, zipcode, city, country), id) => UserCredentials(id, userID, firstName, lastName, street, zipcode, city, country) }
      ) += (userID, firstName, lastName, street, zipcode, city, country)
  }

  def getUserCredentialsByID(id: Long): Future[Option[UserCredentials]] = db.run {
    userCredentials.filter(_.id === id).result.headOption
  }

  def listCredentialsByUserID(userID: Long): Future[Seq[UserCredentials]] = db.run {
    userCredentials.filter(_.userID === userID).result
  }

  def listAllCredentials(): Future[Seq[UserCredentials]] = db.run {
    userCredentials.result
  }

  def update(id: Long, new_userCredentials: UserCredentials): Future[Int] = {
    val userCredentialsUpdate: UserCredentials = new_userCredentials.copy(id)
    db.run(userCredentials.filter(_.id === id).update(userCredentialsUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(userCredentials.filter(_.id === id).delete)


}
