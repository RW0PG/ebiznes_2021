package services

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user_") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def email: Rep[String] = column[String]("email")
    def password: Rep[String] = column[String]("password")

    def * = (id, name, email, password) <> ((User.apply _).tupled, User.unapply)

  }

  val currentUser = TableQuery[UserTable]

  def create(name: String, email: String, password: String): Future[User] = db.run {
    (currentUser.map(current_user => (current_user.email, current_user.name, current_user.password))
      returning currentUser.map(_.id)
      into { case ((name, email, password), id) => User(id, name, email, password) }
      ) += (name, email, password)
  }

  def getUserByID(id: Long): Future[Option[User]] = db.run {
    currentUser.filter(_.id === id).result.headOption
  }

  def listUsers(): Future[Seq[User]] = db.run {
    currentUser.result
  }

  def update(id: Long, user_copy: User): Future[Int] = {
    val currentUserUpdated: User = user_copy.copy(id)
    db.run(currentUser.filter(_.id === id).update(currentUserUpdated))
  }

  def delete(id: Long): Future[Int] = db.run(currentUser.filter(_.id === id).delete)

}
