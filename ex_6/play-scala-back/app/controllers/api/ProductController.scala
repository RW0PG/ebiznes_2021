package controllers.api

import models.Product
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{CategoryRepository, ProductRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductController @Inject()(val ProductRepository: ProductRepository, val categoryRepo: CategoryRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createProduct(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Product].map {
      product =>
        ProductRepository.create(product.stockId, product.categoryId, product.subcategoryId, product.name, product.imageUrl, product.description).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getProductById(id: Long): Action[AnyContent] = Action.async {
    val product = ProductRepository.getByIdOption(id)
    product.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listProducts(): Action[AnyContent] = Action.async {
    val products = ProductRepository.list()
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProductsByStockId(stockId: Long): Action[AnyContent] = Action.async {
    val products = ProductRepository.listByStockId(stockId)
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProductsByCategoryId(categoryId: Long): Action[AnyContent] = Action.async {
    val products = ProductRepository.listByCategoryId(categoryId)
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProductsBySubcategoryId(subcategoryId: Long): Action[AnyContent] = Action.async {
    val products = ProductRepository.listBySubcategoryId(subcategoryId)
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def updateProduct(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].map {
      product =>
        ProductRepository.update(product.id, product).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteProduct(id: Long): Action[AnyContent] = Action.async {
    ProductRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
