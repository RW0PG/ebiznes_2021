package services

import models.ProductCategory
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductCategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[ProductCategory](tag, "category") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def categoryName: Rep[String] = column[String]("categoryName")

    def * = (id, categoryName) <> ((ProductCategory.apply _).tupled, ProductCategory.unapply)
  }

  val category = TableQuery[CategoryTable]

  def create(categoryName: String): Future[ProductCategory] = db.run {
    (category.map(c => (c.categoryName))
      returning category.map(_.id)
      into { case ((name, description, category), id) => Category(id, name, description, category) }
      ) += (name, createdAt, updatedAt)
  }

  def getCategoryByID(id: Long): Future[Option[ProductCategory]] = db.run {
    category.filter(_.id === id).result.headOption
  }

  def getCategoryByName(categoryName: String): Future[Option[ProductCategory]] = db.run {
    category.filter(_.categoryName === categoryName).result.headOption
  }


  def listCategories():Future[Seq[ProductCategory]] = db.run {
    category.result
  }

  def update(id: Long, new_category: ProductCategory): Future[Int] = {
    val categoryToUpdate: ProductCategory = new_category.copy(id)
    db.run(category.filter(_.id === id).update(categoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(category.filter(_.id === id).delete)


}
