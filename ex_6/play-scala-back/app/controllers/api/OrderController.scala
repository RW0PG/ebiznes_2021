package controllers.api

import models.Order
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.OrderRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(val OrderRepository: OrderRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createOrder(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Order].map {
      order =>
        OrderRepository.create(order.userId, order.addressId, order.paymentId, order.voucherId).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }


  def getOrderById(id: Long): Action[AnyContent] = Action.async {
    val order = OrderRepository.getByIdOption(id)
    order.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listOrdersByPaymentId(paymentId: Long): Action[AnyContent] = Action.async {
    val orders = OrderRepository.listByPaymentId(paymentId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrders(): Action[AnyContent] = Action.async {
    val orders = OrderRepository.list()
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByUserId(userId: Long): Action[AnyContent] = Action.async {
    val orders = OrderRepository.listByUserId(userId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByAddressId(addressId: Long): Action[AnyContent] = Action.async {
    val orders = OrderRepository.listByAddressId(addressId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByVoucherId(voucherId: Long): Action[AnyContent] = Action.async {
    val orders = OrderRepository.listByVoucherId(voucherId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def updateOrder(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Order].map {
      order =>
        OrderRepository.update(order.id, order).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action.async {
    OrderRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
