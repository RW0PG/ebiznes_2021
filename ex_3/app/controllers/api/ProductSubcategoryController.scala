package controllers.api

import akka.actor.ActorSystem
import models.MockupMethod
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

@Singleton
class ProductSubcategoryController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def mockupMethodForEx2(delayTime: FiniteDuration, name: String = "mockup_name"): Future[MockupMethod] = {
    val mockup = MockupMethod("mockup_ID", name)
    val promise: Promise[MockupMethod] = Promise[MockupMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(mockup)
    } (actorSystem.dispatcher)
    promise.future
  }

  def createProductSubcategory(): Action[AnyContent] = Action.async { implicit request =>
    println("createProductSubcategory, ProductSubcategory: ", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def getProductSubcategory(id: String): Action[AnyContent] = Action.async {
    println("getProductSubcategory, getting ProductSubcategory by id: ", id)
    mockupMethodForEx2(30.millisecond).map {returned_value => Ok(Json.toJson(returned_value))}
  }

  def updateProductSubcategory(): Action[AnyContent] = Action.async { implicit request =>
    println("updateProductSubcategory, updating ProductSubcategory", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def deleteProductSubcategory(id: String): Action[AnyContent] = Action.async {
    println("deleteProductSubcategory, deleting ProductSubcategory with id:", id)
    mockupMethodForEx2(30.millisecond).map { returned_value => Ok(Json.toJson(returned_value)) }
  }
}