package controllers.api

import play.api.libs.json.Json
import play.api.mvc._
import services.UserRepository

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(val UserRepository: UserRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getUserById(id: Long): Action[AnyContent] = Action.async {
    val user = UserRepository.getByIdOption(id)
    user.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find user with this id")
    }
  }

  def listUsers(): Action[AnyContent] = Action.async {
    val allUsers = UserRepository.list()
    allUsers.map { users =>
      Ok(Json.toJson(users))
    }
  }
}
