package controllers.view

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.UserRepository
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserController @Inject()(userRepository: UserRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def createUser(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepository.listUsers()
    users.map((_: Seq[User]) => Ok(views.html.user_create(userForm)))
  }

  def createUserHandle(): Action[AnyContent] = Action.async {implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user_create(errorForm))
        )
      },
      user => {
        userRepository.create(user.name, user.email, user.password).map { _ =>
          Redirect("/form/user/list")
        }
      }
    )
  }

  def getUsers: Action[AnyContent] = Action.async {
    implicit request =>
      userRepository.listUsers().map(users => Ok(views.html.users_all(users)))
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val user = userRepository.getUserByID(id)
    user.map(user => {
      val prodForm = updateUserForm.fill(UpdateUserForm(user.get.id, user.get.name, user.get.email, user.get.password))
      Ok(views.html.user_update(prodForm))
    })
  }

  def updateUserHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user_update(errorForm))
        )
      },
      user => {
        userRepository.update(user.id, User(user.id, user.email, user.email, user.password)).map { _ =>
          Redirect("/form/user/list")
        }
      }
    )
  }

  def deleteUser(id: Long): Action[AnyContent] = Action  {
    userRepository.delete(id)
    Redirect("/form/user/list")
  }
}

case class CreateUserForm(name: String, email: String, password: String)
case class UpdateUserForm(id: Long,name: String, email: String, password: String)
