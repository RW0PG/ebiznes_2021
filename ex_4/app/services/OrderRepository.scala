package services

import models.Order
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val UserRepository: UserRepository, val CredentialsRepository: UserCredentialsRepository, val PaymentRepository: PaymentRepository, val CouponsRepository: CouponsRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import UserRepository.UserTable
  import CredentialsRepository.UserCredentialsTable
  import PaymentRepository.PaymentTable
  import CouponsRepository.CouponsTable

  val user = TableQuery[UserTable]
  val address = TableQuery[UserCredentialsTable]
  val payment = TableQuery[PaymentTable]
  val voucher = TableQuery[CouponsTable]


  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userID = column[Long]("user_id")

    def userFK = foreignKey("user_fk", userID, user)(_.id)

    def userCredentialsID = column[Long]("credentials_id")

    def paymentID = column[Long]("payment_id")

    def paymentFK = foreignKey("payment_id_fk", paymentID, payment)(_.id)

    def couponID = column[Long]("voucher_id", O.Default(0))

    def couponFK = foreignKey("voucher_id_fk", couponID, voucher)(_.id)

    def * = (id, userID, userCredentialsID, paymentID, couponID) <> ((Order.apply _).tupled, Order.unapply)
  }

  val order = TableQuery[OrderTable]

  def create(userID: Long, credentialsID: Long, paymentID: Long, couponID: Long): Future[Order] = db.run {
    (order.map(o => (o.userID, o.userCredentialsID, o.paymentID, o.couponID))
      returning order.map(_.id)
      into { case ((userID, credentialsID, paymentID, couponID), id) => Order(id, userID, credentialsID, paymentID, couponID) }
      ) += (userID, credentialsID, paymentID, couponID)
  }

  def getOrdersByID(id: Long): Future[Option[Order]] = db.run {
    order.filter(_.id === id).result.headOption
  }

  def listByID(ids: Seq[Long]): Future[Seq[Order]] = db.run {
    order.filter(_.id.inSet(ids)).result
  }

  def listAllOrders(): Future[Seq[Order]] = db.run {
    order.result
  }

  def listByUserID(userID: Long): Future[Seq[Order]] = db.run {
    order.filter(_.userID === userID).result
  }

  def listByAddressId(userCredentialsID: Long): Future[Seq[Order]] = db.run {
    order.filter(_.userCredentialsID === userCredentialsID).result
  }

  def listByPaymentId(paymentID: Long): Future[Seq[Order]] = db.run {
    order.filter(_.paymentID === paymentID).result
  }

  def listByVoucherId(couponID: Long): Future[Seq[Order]] = db.run {
    order.filter(_.couponID === couponID).result
  }

  def update(id: Long, newOrder: Order): Future[Int] = {
    val orderToUpdate: Order = newOrder.copy(id)
    db.run(order.filter(_.id === id).update(orderToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(order.filter(_.id === id).delete)



}
