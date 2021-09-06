package controllers.api

import akka.actor.ActorSystem
import models.MockupMethod
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

@Singleton
class UserController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def mockupMethodForEx2(delayTime: FiniteDuration, name: String = "mockup_name"): Future[MockupMethod] = {
    val mockup = MockupMethod("mockup_ID", name)
    val promise: Promise[MockupMethod] = Promise[MockupMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(mockup)
    } (actorSystem.dispatcher)
    promise.future
  }

  def createUser(): Action[AnyContent] = Action.async { implicit request =>
    println("createUser, creating user:", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }
  def getUser(id: Long): Action[AnyContent] = Action.async {
    println("getUser, getting user by its id:", id)
    mockupMethodForEx2(30.millisecond).map { returned_value => Ok(Json.toJson(returned_value)) }
  }
  def updateUser(): Action[AnyContent] = Action.async { implicit request =>
    println("updateUser, updating user:", request.body)
    mockupMethodForEx2(30.millisecond, request.body.asJson.get("name").as[String]).map { returned_value => Ok(Json.toJson(returned_value)) }
  }

  def deleteUser(id: String): Action[AnyContent] = Action.async {
    println("deleteUser, user deleted with id:", id)
    mockupMethodForEx2(30.millisecond).map { returned_value => Ok(Json.toJson(returned_value)) }
  }
}


