package controllers.api

import models.Stock
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.StockRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockController @Inject()(val StockRepository: StockRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createStock(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Stock].map {
      stock =>
        StockRepository.create(stock.unitPrice, stock.totalPrice, stock.totalStock).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getStockById(id: Long): Action[AnyContent] = Action.async {
    val stock = StockRepository.getByIdOption(id)
    stock.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def listStocks(): Action[AnyContent] = Action.async {
    val stocks = StockRepository.list()
    stocks.map { stocks =>
      Ok(Json.toJson(stocks))
    }
  }

  def updateStock(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Stock].map {
      stock =>
        StockRepository.update(stock.id, stock).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteStock(id: Long): Action[AnyContent] = Action.async {
    StockRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
