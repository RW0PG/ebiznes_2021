package controllers.api

import models.OrderProduct
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.OrderProductRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderProductController @Inject()(val OrderProductRepository: OrderProductRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createOrderProduct(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[OrderProduct].map {
      orderProduct =>
        OrderProductRepository.create(orderProduct.orderId, orderProduct.productId, orderProduct.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }


  def getOrderProductById(id: Long): Action[AnyContent] = Action.async {
    val orderProduct = OrderProductRepository.getByIdOption(id)
    orderProduct.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listProductsByOrderId(orderId: Long): Action[AnyContent] = Action.async {
    val orderProducts = OrderProductRepository.listProductsByOrderId(orderId)
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }


  def listOrderProducts(): Action[AnyContent] = Action.async {
    val orderProducts = OrderProductRepository.list()
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def listOrdersByProductId(productId: Long): Action[AnyContent] = Action.async {
    val orderProducts = OrderProductRepository.listOrdersByProductId(productId)
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def updateOrderProduct(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[OrderProduct].map {
      orderProduct =>
        OrderProductRepository.update(orderProduct.id, orderProduct).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteOrderProduct(id: Long): Action[AnyContent] = Action.async {
    OrderProductRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
