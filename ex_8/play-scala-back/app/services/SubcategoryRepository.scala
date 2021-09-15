package services

import models.Subcategory
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubcategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import categoryRepository.CategoryTable

  val category_ = TableQuery[CategoryTable]

  class SubcategoryTable(tag: Tag) extends Table[Subcategory](tag, "subcategory") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def categoryId = column[Long]("category_id")
    def categoryFk = foreignKey("category_fk", categoryId, category_)(_.id)
    def name = column[String]("name")


    def * = (id, categoryId, name) <> ((Subcategory.apply _).tupled, Subcategory.unapply)
  }


  val subcategory = TableQuery[SubcategoryTable]


  def create(name: String, categoryId: Long): Future[Subcategory] = db.run {
    (subcategory.map(s => (s.name, s.categoryId))
      returning subcategory.map(_.id)
      into { case ((name, categoryId), id) => Subcategory(id, categoryId, name) }
      ) += (name, categoryId)
  }

  def getByIdOption(id: Long): Future[Option[Subcategory]] = db.run {
    subcategory.filter(_.id === id).result.headOption
  }

  def getByNameOption(name: String): Future[Option[Subcategory]] = db.run {
    subcategory.filter(_.name === name).result.headOption
  }

  def list(): Future[Seq[Subcategory]] = db.run {
    subcategory.result
  }

  def listByCategoryId(categoryId: Long): Future[Seq[Subcategory]] = db.run {
    subcategory.filter(_.categoryId === categoryId).result
  }

  def update(id: Long, new_subcategory: Subcategory): Future[Int] = {
    val subcategoryToUpdate: Subcategory = new_subcategory.copy(id)
    db.run(subcategory.filter(_.id === id).update(subcategoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(subcategory.filter(_.id === id).delete)
}
