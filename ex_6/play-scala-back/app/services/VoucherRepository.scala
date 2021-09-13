package services

import models.Voucher
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class VoucherTable(tag: Tag) extends Table[Voucher](tag, "voucher") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def code: Rep[String] = column[String]("code")
    def amount: Rep[Int] = column[Int]("amount")
    def usages: Rep[Int] = column[Int]("usages")

    def * = (id, code, amount, usages) <> ((Voucher.apply _).tupled, Voucher.unapply)
  }

  val voucher = TableQuery[VoucherTable]

  def create(code: String, amount: Int, usages: Int): Future[Voucher] = db.run {
    (voucher.map(v => (v.code, v.amount, v.usages))
      returning voucher.map(_.id)
      into { case ((code, amount, usages), id) => Voucher(id, code, amount, usages) }
      ) += (code, amount, usages)
  }

  def getByIdOption(id: Long): Future[Option[Voucher]] = db.run {
    voucher.filter(_.id === id).result.headOption
  }

  def getByCodeOption(code: String): Future[Option[Voucher]] = db.run {
    voucher.filter(_.code === code).result.headOption
  }

  def list(): Future[Seq[Voucher]] = db.run {
    voucher.result
  }

  def update(id: Long, new_voucher: Voucher): Future[Int] = {
    val voucherToUpdate: Voucher = new_voucher.copy(id)
    db.run(voucher.filter(_.id === id).update(voucherToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(voucher.filter(_.id === id).delete)
}
