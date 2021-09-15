package services

import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository, val creditCardRepository: CreditCardRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("user_id")
    def userFk = foreignKey("user_fk", userId, user_)(_.id)
    def creditCardId = column[Long]("credit_card_id")
    def creditCardFk = foreignKey("credit_card_id_fk", creditCardId, creditCard_)(_.id)
    def amount: Rep[Int] = column[Int]("amount")

    def * = (id, userId, creditCardId, amount) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  import creditCardRepository.CreditCardTable
  import userRepository.UserTable

  val payment = TableQuery[PaymentTable]
  val user_ = TableQuery[UserTable]
  val creditCard_ = TableQuery[CreditCardTable]

  def create(userId: Long, creditCardId: Long, amount: Int, createdAt: Timestamp = Timestamp.from(Instant.now()), updatedAt: Timestamp = Timestamp.from(Instant.now())): Future[Payment] = db.run {
    (payment.map(p => (p.userId, p.creditCardId, p.amount))
      returning payment.map(_.id)
      into { case ((userId, creditCardId, amount), id) => Payment(id, userId, creditCardId, amount) }
      ) += (userId, creditCardId, amount)
  }

  def getByIdOption(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def listByUserId(userId: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.userId === userId).result
  }

  def listByCreditCardId(creditCardId: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.creditCardId === creditCardId).result
  }

  def update(id: Long, new_payment: Payment): Future[Int] = {
    val paymentToUpdate: Payment = new_payment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(payment.filter(_.id === id).delete)
}
