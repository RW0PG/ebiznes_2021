package controllers.api

import models.CreditCard
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.CreditCardRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreditCardController @Inject()(val CreditCardRepository: CreditCardRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createCreditCard(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreditCard].map {
      creditCard =>
        CreditCardRepository.create(creditCard.userId, creditCard.cardholderName, creditCard.number, creditCard.expDate, creditCard.cvcCode).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getCreditCardById(id: Long): Action[AnyContent] = Action.async {
    val creditCard = CreditCardRepository.getByIdOption(id)
    creditCard.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listCreditCards(): Action[AnyContent] = Action.async {
    val creditCards = CreditCardRepository.list()
    creditCards.map { creditCards =>
      Ok(Json.toJson(creditCards))
    }
  }

  def listCreditCardsByUserId(userId: Long): Action[AnyContent] = Action.async {
    val creditCards = CreditCardRepository.listByUserId(userId)
    creditCards.map { creditCards =>
      Ok(Json.toJson(creditCards))
    }
  }


  def updateCreditCard(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreditCard].map {
      creditCard =>
        CreditCardRepository.update(creditCard.id, creditCard).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteCreditCard(id: Long): Action[AnyContent] = Action.async {
    CreditCardRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
