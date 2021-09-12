package services

import models.Coupon
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CouponsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CouponsTable(tag: Tag) extends Table[Coupon](tag, "coupons") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def couponValue: Rep[Double] = column[Double]("value")

    def couponUsages: Rep[Int] = column[Int]("coupon usages")


    def * = (id, couponValue, couponUsages) <> ((Coupon.apply _).tupled, Coupon.unapply)
  }

  val coupon = TableQuery[CouponsTable]

  def create(couponValue: Double, couponUsages: Int): Future[Coupon] = db.run {
    (coupon.map(c => (c.couponValue, c.couponUsages))
      returning coupon.map(_.id)
      into { case ((couponValue, couponUsages), id) => Coupon(id, couponValue, couponUsages) }
      ) += (couponValue, couponUsages)
  }

  def getCouponByID(id: Long): Future[Option[Coupon]] = db.run {
    coupon.filter(_.id === id).result.headOption
  }

  def listCoupons(): Future[Seq[Coupon]] = db.run {
    coupon.result
  }

  def update(id: Long, new_coupon: Coupon): Future[Int] = {
    val couponToUpdate: Coupon = new_coupon.copy(id)
    db.run(coupon.filter(_.id === id).update(couponToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(coupon.filter(_.id === id).delete)

}