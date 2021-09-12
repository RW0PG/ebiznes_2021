package services

import models.ProductDescription
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductDescriptionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class DescriptionTable(tag: Tag) extends Table[ProductDescription](tag, "description") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def productName: Rep[String] = column[String]("productName")
    def text: Rep[String] = column[String]("text")

    def * = (id, productName, text) <> ((ProductDescription.apply _).tupled, ProductDescription.unapply)
  }

  val description = TableQuery[DescriptionTable]

  def create(productName: String, text: String): Future[ProductDescription] = db.run {
    (description.map(v => (v.text, v.productName))
      returning description.map(_.id)
      into { case ((productName, text), id) => ProductDescription(id, productName, text) }
      ) += (productName, text)
  }

  def getDescriptionByID(id: Long): Future[Option[ProductDescription]] = db.run {
    description.filter(_.id === id).result.headOption
  }

  def getDescriptionByProductName(productName: String): Future[Option[ProductDescription]] = db.run {
    description.filter(_.productName === productName).result.headOption
  }

  def ListAllDescriptions(): Future[Seq[ProductDescription]] = db.run {
    description.result
  }

  def update(id: Long, new_stock: ProductDescription): Future[Int] = {
    val descriptionToUpdate: ProductDescription = new_stock.copy(id)
    db.run(description.filter(_.id === id).update(descriptionToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(description.filter(_.id === id).delete)
}