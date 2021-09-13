package controllers.view

import models.{Category, Product, Stock, Subcategory}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{CategoryRepository, ProductRepository, StockRepository, SubcategoryRepository}

import javax.inject._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ProductController @Inject()(ProductRepository: ProductRepository, StockRepository: StockRepository, CategoryRepository: CategoryRepository, SubcategoryRepository: SubcategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "stockId" -> longNumber,
      "categoryId" -> longNumber,
      "subcategoryId" -> longNumber,
      "name" -> nonEmptyText,
      "imageUrl" -> nonEmptyText,
      "description" -> nonEmptyText,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "stockId" -> longNumber,
      "categoryId" -> longNumber,
      "subcategoryId" -> longNumber,
      "name" -> nonEmptyText,
      "imageUrl" -> nonEmptyText,
      "description" -> nonEmptyText,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def fetchData(): Unit = {

    StockRepository.list().onComplete {
      case Success(stocks) => stockList = stocks
      case Failure(e) => print("error while listing stocks", e)
    }

    CategoryRepository.list().onComplete {
      case Success(category) => categoryList = category
      case Failure(e) => print("error while listing categories", e)
    }

    SubcategoryRepository.list().onComplete {
      case Success(subcategory) => subcategoryList = subcategory
      case Failure(e) => print("error while listing subcategories", e)
    }
  }

  var stockList: Seq[Stock] = Seq[Stock]()
  var categoryList: Seq[Category] = Seq[Category]()
  var subcategoryList: Seq[Subcategory] = Seq[Subcategory]()

  fetchData()

  def createProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product_create(errorForm, stockList, categoryList, subcategoryList))
        )
      },
      product => {
        ProductRepository.create(product.stockId, product.categoryId, product.subcategoryId, product.name, product.imageUrl, product.description).map { _ =>
          Redirect("/form/product/list")
        }
      }
    )
  }

  def listProducts: Action[AnyContent] = Action.async { implicit request =>
    ProductRepository.list().map(products => Ok(views.html.product_list(products)))
  }

  def createProduct(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stocks = Await.result(StockRepository.list(), 1.second)
    val categories = Await.result(CategoryRepository.list(), 1.second)
    val subcategories = SubcategoryRepository.list()

    subcategories.map(subcategory => Ok(views.html.product_create(productForm, stocks, categories, subcategory)))
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val product = ProductRepository.getByIdOption(id)
    product.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.get.id, product.get.stockId, product.get.categoryId, product.get.subcategoryId, product.get.name, product.get.imageUrl, product.get.description))
      Ok(views.html.product_update(prodForm, stockList, categoryList, subcategoryList))
    })
  }

  def updateProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product_update(errorForm, stockList, categoryList, subcategoryList))
        )
      },
      product => {
        ProductRepository.update(product.id, Product(product.id, product.stockId, product.categoryId, product.subcategoryId, product.name, product.imageUrl, product.description)).map { _ =>
          Redirect("/form/product/list")
        }
      }
    )
  }

  def deleteProduct(id: Long): Action[AnyContent] = Action {
    ProductRepository.delete(id)
    Redirect("/form/product/list")
  }
}

case class CreateProductForm(stockId: Long, categoryId: Long, subcategoryId: Long, name: String, imageUrl: String, description: String)

case class UpdateProductForm(id: Long, stockId: Long, categoryId: Long, subcategoryId: Long, name: String, imageUrl: String, description: String)
