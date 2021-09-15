package controllers.view

import models.Stock
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.StockRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockController @Inject()(StockRepository: StockRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val stockForm: Form[CreateStockForm] = Form {
    mapping(
      "unitPrice" -> number,
      "totalPrice" -> number,
      "totalStock" -> number,
    )(CreateStockForm.apply)(CreateStockForm.unapply)
  }

  val updateStockForm: Form[UpdateStockForm] = Form {
    mapping(
      "id" -> longNumber,
      "unitPrice" -> number,
      "totalPrice" -> number,
      "totalStock" -> number,
    )(UpdateStockForm.apply)(UpdateStockForm.unapply)
  }

  def createStock(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stocks = StockRepository.list()
    stocks.map(_ => Ok(views.html.stock_create(stockForm)))
  }

  def createStockHandle(): Action[AnyContent] = Action.async { implicit request =>
    stockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock_create(errorForm))
        )
      },
      stock => {
        StockRepository.create(stock.unitPrice, stock.totalPrice, stock.totalStock).map { _ =>
          Redirect("/form/stock/list")
        }
      }
    )
  }

  def listStocks: Action[AnyContent] = Action.async { implicit request =>
    StockRepository.list().map(stocks => Ok(views.html.stock_list(stocks)))
  }

  def updateStock(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stock = StockRepository.getByIdOption(id)
    stock.map(stock => {
      val prodForm = updateStockForm.fill(UpdateStockForm(stock.get.id, stock.get.unitPrice, stock.get.totalPrice, stock.get.totalStock))
      Ok(views.html.stock_update(prodForm))
    })
  }

  def updateStockHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateStockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock_update(errorForm))
        )
      },
      stock => {
        StockRepository.update(stock.id, Stock(stock.id, stock.unitPrice, stock.totalPrice, stock.totalStock)).map { _ =>
          Redirect("/form/stock/list")
        }
      }
    )
  }

  def deleteStock(id: Long): Action[AnyContent] = Action {
    StockRepository.delete(id)
    Redirect("/form/stock/list")
  }
}

case class CreateStockForm(unitPrice: Int, totalPrice: Int, totalStock: Int)

case class UpdateStockForm(id: Long, unitPrice: Int, totalPrice: Int, totalStock: Int)
