package controllers.api

import models.Category
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.CategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(val CategoryRepository: CategoryRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createCategory(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Category].map {
      category =>
        CategoryRepository.create(category.name).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getCategoryById(id: Long): Action[AnyContent] = Action.async {
    val category = CategoryRepository.getByIdOption(id)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def getCategoryByName(name: String): Action[AnyContent] = Action.async {
    val category = CategoryRepository.getByNameOption(name)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("category with given name cannot be found")
    }
  }

  def listCategories(): Action[AnyContent] = Action.async {
    val categories = CategoryRepository.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def updateCategory(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Category].map {
      category =>
        CategoryRepository.update(category.id, category).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action.async {
    CategoryRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

