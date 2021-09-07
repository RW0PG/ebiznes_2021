package controllers.api


import models.User
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UserRepository
import javax.inject._
import scala.concurrent.{ExecutionContext, Future, Promise}



@Singleton
class UserController @Inject()(userRepository: UserRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

    def createUser(): Action[JsValue] = Action.async(parse.json) {
      implicit request =>
        request.body.validate[User].map {
          user =>
            userRepository.create(user.name, user.email, user.password).map { res =>
              Ok(Json.toJson(res))
            }
        }.getOrElse(Future.successful(BadRequest("wrong data")))
    }

    def getUserById(id: Long): Action[AnyContent] = Action.async {
      val user = userRepository.getUserByID(id)
      user.map {
        case Some(res) => Ok(Json.toJson(res))
        case None => NotFound("missing user with given id")
      }
    }

    def getAllUsers: Action[AnyContent] = Action.async {
      val users = userRepository.listUsers()
      users.map {
        users =>
          Ok(Json.toJson(users))
      }
    }

    def updateUser(): Action[JsValue] = Action.async(parse.json) {
      implicit request =>
        request.body.validate[User].map {
          user =>
            userRepository.create(user.name, user.email, user.password).map {
              res =>
                Ok(Json.toJson(res))
            }
        }.getOrElse(Future.successful(BadRequest("incorrect data")))
    }

    def deleteUser(id: Long): Action[AnyContent] = Action.async {
      userRepository.delete(id).map {
        res =>
          Ok(Json.toJson(res))
      }
    }
  }



