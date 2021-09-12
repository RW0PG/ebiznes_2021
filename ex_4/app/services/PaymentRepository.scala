package services

import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val UserRepository: UserRepository, val PaymentType: PaymentCardRepository)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import UserRepository.UserTable
  import PaymentType.PaymentCardTable

  val user = TableQuery[UserTable]
  val card = TableQuery[PaymentCardTable]


  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userID = column[Long]("user_id")
    def userFK = foreignKey("user_fk", userID, user)(_.id)
    def paymentCardID = column[Long]("card_id")
    def paymentCardFK = foreignKey("card_id_fk", paymentCardID, card)(_.id)
    def total: Rep[Double] = column[Double]("amount")


    def * = (id, userID, paymentCardID, total) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  val payment = TableQuery[PaymentTable]

  def create(userID: Long, paymentCardID: Long, total: Double): Future[Payment] = db.run {
    (payment.map(p => (p.userID, p.paymentCardID, p.total))
      returning payment.map(_.id)
      into { case ((userID, paymentCardID, total), id) => Payment(id, userID, paymentCardID, total) }
      ) += (userID, paymentCardID, total)
  }

  def getPaymentByID(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def listAllPayments(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def listByUserID(userID: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.userID === userID).result
  }

  def listByCreditCardID(cardID: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.paymentCardID === cardID ).result
  }

  def update(id: Long, newPayment: Payment): Future[Int] = {
    val paymentToUpdate: Payment = newPayment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(payment.filter(_.id === id).delete)

}
