package controllers.api

import models.Subcategory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.SubcategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubcategoryController @Inject()(val SubcategoryRepository: SubcategoryRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {


  def createSubcategory(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Subcategory].map {
      subcategory =>
        SubcategoryRepository.create(subcategory.name, subcategory.categoryId).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getSubcategoryById(id: Long): Action[AnyContent] = Action.async {
    val subcategory = SubcategoryRepository.getByIdOption(id)
    subcategory.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def getSubcategoryByName(name: String): Action[AnyContent] = Action.async {
    val subcategory = SubcategoryRepository.getByNameOption(name)
    subcategory.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find")
    }
  }

  def listSubcategories(): Action[AnyContent] = Action.async {
    val subcategories = SubcategoryRepository.list()
    subcategories.map { subcategories =>
      Ok(Json.toJson(subcategories))
    }
  }

  def listSubcategoriesByCategoryId(categoryId: Long): Action[AnyContent] = Action.async {
    val subcategories = SubcategoryRepository.listByCategoryId(categoryId)
    subcategories.map { subcategories =>
      Ok(Json.toJson(subcategories))
    }
  }

  def updateSubcategory(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Subcategory].map {
      subcategory =>
        SubcategoryRepository.update(subcategory.id, subcategory).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteSubcategory(id: Long): Action[AnyContent] = Action.async {
    SubcategoryRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
