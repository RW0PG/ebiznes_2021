package controllers.api

import akka.actor.ActorSystem
import models.MockupMethod
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

@Singleton
class ProductDescriptionController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def mockupMethodForEx2(delayTime: FiniteDuration, name: String = "mockup_name"): Future[MockupMethod] = {
    val mockup = MockupMethod("mockup_ID", name)
    val promise: Promise[MockupMethod] = Promise[MockupMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(mockup)
    } (actorSystem.dispatcher)
    promise.future
  }

  def createProductDescription(): Action[AnyContent] = Action.async { implicit request =>
    println("createProductDescription, product description: ", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def getProductDescription(id: String): Action[AnyContent] = Action.async {
    println("getProductDescription, getting product description by id: ", id)
    mockupMethodForEx2(30.millisecond).map {returned_value => Ok(Json.toJson(returned_value))}
  }

  def updateProductDescription(): Action[AnyContent] = Action.async { implicit request =>
    println("updateProductDescription, updating product description", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def deleteProductDescription(id: String): Action[AnyContent] = Action.async {
    println("deleteProductDescription, deleting product description with id:", id)
    mockupMethodForEx2(30.millisecond).map { returned_value => Ok(Json.toJson(returned_value)) }
  }
}