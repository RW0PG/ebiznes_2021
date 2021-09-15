package controllers.api

import models.UserAddress
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UserAddressRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressController @Inject()(val UserAddressRepository: UserAddressRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createUserAddress(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserAddress].map {
      userAddress =>
        UserAddressRepository.create(userAddress.userId, userAddress.firstname, userAddress.lastname, userAddress.address, userAddress.zipcode, userAddress.city, userAddress.country).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getUserAddressById(id: Long): Action[AnyContent] = Action.async {
    val userAddress = UserAddressRepository.getByIdOption(id)
    userAddress.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listUserAddresses(): Action[AnyContent] = Action.async {
    val userAddresses = UserAddressRepository.list()
    userAddresses.map { userAddresses =>
      Ok(Json.toJson(userAddresses))
    }
  }

  def listUserAddressesByUserId(userId: Long): Action[AnyContent] = Action.async {
    val userAddresses = UserAddressRepository.listByUserId(userId)
    userAddresses.map { userAddresses =>
      Ok(Json.toJson(userAddresses))
    }
  }

  def updateUserAddress(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UserAddress].map {
      userAddress =>
        UserAddressRepository.update(userAddress.id, userAddress).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteUserAddress(id: Long): Action[AnyContent] = Action.async {
    UserAddressRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
