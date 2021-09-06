package controllers.api

import akka.actor.ActorSystem
import models.MockupMethod
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

@Singleton
class ProductCategoryController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def mockupMethodForEx2(delayTime: FiniteDuration, name: String = "mockup_name"): Future[MockupMethod] = {
    val mockup = MockupMethod("mockup_ID", name)
    val promise: Promise[MockupMethod] = Promise[MockupMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(mockup)
    } (actorSystem.dispatcher)
    promise.future
  }

  def createProductCategory(): Action[AnyContent] = Action.async { implicit request =>
    println("createProductCategory, Product Category: ", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def getProductCategory(id: String): Action[AnyContent] = Action.async {
    println("getProductCategory, getting product category by id: ", id)
    mockupMethodForEx2(30.millisecond).map {returned_value => Ok(Json.toJson(returned_value))}
  }

  def updateProductCategory(): Action[AnyContent] = Action.async { implicit request =>
    println("updateProductCategory, updating product category", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def deleteProductCategory(id: String): Action[AnyContent] = Action.async {
    println("deleteProductCategory, deleting product category with id:", id)
    mockupMethodForEx2(30.millisecond).map { returned_value => Ok(Json.toJson(returned_value)) }
  }
}