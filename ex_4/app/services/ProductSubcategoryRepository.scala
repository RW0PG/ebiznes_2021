package services

import models.ProductSubcategory
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductSubcategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val CategoryRepository: ProductCategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import CategoryRepository.CategoryTable

  val category = TableQuery[CategoryTable]

  class SubcategoryTable(tag: Tag) extends Table[ProductSubcategory](tag, "subcategory") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def categoryID = column[Long]("category_id")

    def category_fk = foreignKey("category_fk", categoryID, category)(_.id)

    def subCategoryName = column[String]("name")

    def * = (id, categoryID, subCategoryName) <> ((ProductSubcategory.apply _).tupled, ProductSubcategory.unapply)
  }

  val subcategory = TableQuery[SubcategoryTable]

  def create(categoryID: Long, subcategoryName: String): Future[ProductSubcategory] = db.run {
    (subcategory.map(s => (s.categoryID, s.subCategoryName))
      returning subcategory.map(_.id)
      into { case ((categoryID, subcategoryName), id) => ProductSubcategory(id, categoryID, subcategoryName) }
      ) += (categoryID, subcategoryName)
  }

  def getSubcategoryByID(id: Long): Future[Option[ProductSubcategory]] = db.run {
    subcategory.filter(_.id === id).result.headOption
  }

  def getSubcategoryByName(subcategoryName: String): Future[Option[ProductSubcategory]] = db.run {
    subcategory.filter(_.subCategoryName === subcategoryName).result.headOption
  }

  def listSubCategories(): Future[Seq[ProductSubcategory]] = db.run {
    subcategory.result
  }

  def listByCategoryID(categoryID: Long): Future[Seq[ProductSubcategory]] = db.run {
    subcategory.filter(_.categoryID === categoryID).result
  }

  def update(id: Long, new_subcategory: ProductSubcategory): Future[Int] = {
    val subcategoryToUpdate: ProductSubcategory = new_subcategory.copy(id)
    db.run(subcategory.filter(_.id === id).update(subcategoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(subcategory.filter(_.id === id).delete)

}

