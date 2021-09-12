package services

import models.{Order, OrderProduct, Product, ProductOrder}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ProductOrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val OrderRepository: OrderRepository, val ProductRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import ProductRepository.ProductTable
  import OrderRepository.OrderTable

  val order = TableQuery[OrderTable]
  val product = TableQuery[ProductTable]

  class OrderProductTable(tag: Tag) extends Table[ProductOrder](tag, "order_product") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def orderID = column[Long]("order_id")
    def orderFK = foreignKey("order_fk", orderID, order)(_.id)
    def productID = column[Long]("product_id")
    def productFK = foreignKey("product_id_fk", productID, product)(_.id)
    def amount: Rep[Int] = column[Int]("amount")

    def * = (id, orderID, productID, amount) <> ((ProductOrder.apply _).tupled, ProductOrder.unapply)
  }



  val orderProduct = TableQuery[OrderProductTable]


  def create(orderID: Long, productID: Long, amount: Int): Future[ProductOrder] = db.run {
    (orderProduct.map(p => (p.orderID, p.productID, p.amount))
      returning orderProduct.map(_.id)
      into { case ((orderID, productID, amount), id) => ProductOrder(id, orderID, productID, amount) }
      ) += (orderID, productID, amount)
  }

  def getProductOrdersByID(id: Long): Future[Option[ProductOrder]] = db.run {
    orderProduct.filter(_.id === id).result.headOption
  }

  def listAllProductOrders(): Future[Seq[ProductOrder]] = db.run {
    orderProduct.result
  }

  def listByOrderID(orderId: Long): Future[Seq[ProductOrder]] = db.run {
    orderProduct.filter(_.orderID === orderId).result
  }

  def listByProductId(productId: Long): Future[Seq[ProductOrder]] = db.run {
    orderProduct.filter(_.productID === productId).result
  }

  def update(id: Long, newOrderProduct: ProductOrder): Future[Int] = {
    val orderProductToUpdate: ProductOrder = newOrderProduct.copy(id)
    db.run(orderProduct.filter(_.id === id).update(orderProductToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(orderProduct.filter(_.id === id).delete)
}