package controllers.api

import models.Payment
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.PaymentRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentController @Inject()(val PaymentRepository: PaymentRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createPayment(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Payment].map {
      payment =>
        PaymentRepository.create(payment.userId, payment.creditCardId, payment.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }


  def getPaymentById(id: Long): Action[AnyContent] = Action.async {
    val payment = PaymentRepository.getByIdOption(id)
    payment.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listPaymentsByUserId(userId: Long): Action[AnyContent] = Action.async {
    val payments = PaymentRepository.listByUserId(userId)
    payments.map { payments =>
      Ok(Json.toJson(payments))
    }
  }

  def listPaymentsByCreditCardId(creditCardId: Long): Action[AnyContent] = Action.async {
    val payments = PaymentRepository.listByCreditCardId(creditCardId)
    payments.map { payments =>
      Ok(Json.toJson(payments))
    }
  }

  def listPayments(): Action[AnyContent] = Action.async {

    val payments = PaymentRepository.list()
    payments.map { payments =>
      Ok(Json.toJson(payments))
    }
  }

  def updatePayment(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Payment].map {
      payment =>
        PaymentRepository.update(payment.id, payment).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deletePayment(id: Long): Action[AnyContent] = Action.async {
    PaymentRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
