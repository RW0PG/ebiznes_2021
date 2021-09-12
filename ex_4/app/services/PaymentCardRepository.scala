package services

import models.PaymentCard
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class PaymentCardRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val UserRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import UserRepository.UserTable

  class PaymentCardTable(tag: Tag) extends Table[PaymentCard](tag, "credit_card") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userID = column[Long]("user_id")
    def userFK = foreignKey("user_fk", userID, user)(_.id)
    def cardOwner: Rep[String] = column[String]("cardholder_name")
    def number: Rep[String] = column[String]("number")

    def * = (id, userID, number, cardOwner) <> ((PaymentCard.apply _).tupled, PaymentCard.unapply)
  }
  val card = TableQuery[PaymentCardTable]
  val user = TableQuery[UserTable]

  def create(userID: Long, number: String, cardOwner: String): Future[PaymentCard] = db.run {
    (card.map(c => (c.userID, c.number, c.cardOwner))
      returning card.map(_.id)
      into { case ((userID, number, cardOwner), id) => PaymentCard(id, userID, number, cardOwner) }
      ) += (userID, number, cardOwner)
  }

  def getCardByID(id: Long): Future[Option[PaymentCard]] = db.run {
    card.filter(_.id === id).result.headOption
  }

  def listCardsByUserID(userID: Long): Future[Seq[PaymentCard]] = db.run {
    card.filter(_.userID === userID).result
  }

  def listAllCards(): Future[Seq[PaymentCard]] = db.run {
    card.result
  }

  def update(id: Long, newCreditCard: PaymentCard): Future[Int] = {
    val cardToUpdate: PaymentCard = newCreditCard.copy(id)
    db.run(card.filter(_.id === id).update(cardToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(card.filter(_.id === id).delete)





}
