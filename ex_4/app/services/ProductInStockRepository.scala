package services

import models.ProductInStock
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductInStockRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class StockTable(tag: Tag) extends Table[ProductInStock](tag, "stock") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def amount: Rep[Int] = column[Int]("amount")
    def totalValue: Rep[Double] = column[Double]("totalValue")
    def singleValue: Rep[Double] = column[Double]("singleValue")

    def * = (id, amount, totalValue, singleValue) <> ((ProductInStock.apply _).tupled, ProductInStock.unapply)
  }

  val stock = TableQuery[StockTable]

  def create(amount: Int, totalValue: Double, singleValue: Double): Future[ProductInStock] = db.run {
    (stock.map(s => (s.amount, s.totalValue, s.singleValue))
      returning stock.map(_.id)
      into { case ((amount, totalValue, singleValue), id) => ProductInStock(id, amount, totalValue, singleValue) }
      ) += (amount, totalValue, singleValue)
  }

  def GetProductStockByID(id: Long): Future[Option[ProductInStock]] = db.run {
    stock.filter(_.id === id).result.headOption
  }

  def ListAllStocks(): Future[Seq[ProductInStock]] = db.run {
    stock.result
  }

  def update(id: Long, new_stock: ProductInStock): Future[Int] = {
    val stockToUpdate: ProductInStock = new_stock.copy(id)
    db.run(stock.filter(_.id === id).update(stockToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(stock.filter(_.id === id).delete)

}
