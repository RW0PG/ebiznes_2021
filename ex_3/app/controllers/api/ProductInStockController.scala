package controllers.api

import akka.actor.ActorSystem
import models.MockupMethod
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

@Singleton
class ProductInStockController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def mockupMethodForEx2(delayTime: FiniteDuration, name: String = "mockup_name"): Future[MockupMethod] = {
    val mockup = MockupMethod("mockup_ID", name)
    val promise: Promise[MockupMethod] = Promise[MockupMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(mockup)
    } (actorSystem.dispatcher)
    promise.future
  }

  def createProductInStock(): Action[AnyContent] = Action.async { implicit request =>
    println("createProductInStock, product stock: ", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { msg => Ok(Json.toJson(msg)) }
  }

  def getProductInStock(id: String): Action[AnyContent] = Action.async {
    println("getProductInStock, getting product stock by id: ", id)
    mockupMethodForEx2(30.millisecond).map {msg => Ok(Json.toJson(msg))}
  }

  def updateProductInStock(): Action[AnyContent] = Action.async { implicit request =>
    println("updateProductInStock, updating product stock", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { msg => Ok(Json.toJson(msg)) }
  }

  def deleteProductInStock(id: String): Action[AnyContent] = Action.async {
    println("deleteProductInStock, deleting product stock with id:", id)
    mockupMethodForEx2(30.millisecond).map { msg => Ok(Json.toJson(msg)) }
  }
}