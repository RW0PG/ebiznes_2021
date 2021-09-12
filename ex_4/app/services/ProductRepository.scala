package services

import models.Product
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val StockRepository: ProductInStockRepository, val CategoryRepository: ProductCategoryRepository, val SubcategoryRepository: ProductSubcategoryRepository, val ProductDescription: ProductDescriptionRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import CategoryRepository.CategoryTable
  import StockRepository.StockTable
  import SubcategoryRepository.SubcategoryTable
  import ProductDescription.DescriptionTable

  val stock = TableQuery[StockTable]
  val category = TableQuery[CategoryTable]
  val subcategory = TableQuery[SubcategoryTable]
  val description = TableQuery[DescriptionTable]

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def categoryID = column[Long]("category_id")
    def category_fk = foreignKey("category_id_fk", categoryID, category)(_.id)
    def subcategoryID = column[Long]("subcategory_id", O.Default(0))
    def subcategory_fk = foreignKey("subcategory_id_fk", subcategoryID, subcategory)(_.id)
    def descriptionID: Rep[Long] = column[Long]("description")
    def description_fk = foreignKey("description_id_fk", descriptionID, description)(_.id)
    def stockID = column[Long]("stock_id")
    def stock_fk = foreignKey("stock_fk", stockID, stock)(_.id)



    def * = (id, name, categoryID, subcategoryID, descriptionID, stockID) <> ((Product.apply _).tupled, Product.unapply)
  }


  val product = TableQuery[ProductTable]


  def create(name: String, categoryID: Long, subcategoryID: Long, descriptionID: Long, stockID: Long): Future[Product] = db.run {
    (product.map(p => (p.name, p.categoryID, p.subcategoryID, p.descriptionID, p.stockID))
      returning product.map(_.id)
      into { case ((name, categoryID, subcategoryID, descriptionID, stockID), id) => Product(id, name, categoryID, subcategoryID, descriptionID, stockID) }
      ) += (name, categoryID, subcategoryID, descriptionID, stockID)
  }

  def getProductByID(id: Long): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def listAllProducts(): Future[Seq[Product]] = db.run {
    product.result
  }

  def listAllProductsByID(ids: Seq[Long]): Future[Seq[Product]] = db.run {
    product.filter(_.id.inSet(ids)).result
  }

  def listByCategoryID(categoryID: Long): Future[Seq[Product]] = db.run {
    product.filter(_.categoryID === categoryID).result
  }

  def listBySubcategoryID(subcategoryID: Long): Future[Seq[Product]] = db.run {
    product.filter(_.subcategoryID === subcategoryID).result
  }

  def listAllProductsByStockID(stockID: Long): Future[Seq[Product]] = db.run {
    product.filter(_.stockID === stockID).result
  }

  def listAllProductsByDescriptionID(descriptionID: Long): Future[Seq[Product]] = db.run {
    product.filter(_.descriptionID === descriptionID).result
  }


  def update(id: Long, new_product: Product): Future[Int] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(product.filter(_.id === id).delete)
}