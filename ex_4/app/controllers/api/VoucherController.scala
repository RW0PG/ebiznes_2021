package controllers.api

import models.Voucher
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.VoucherRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherController @Inject()(val VoucherRepository: VoucherRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def createVoucher(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Voucher].map {
      voucher =>
        VoucherRepository.create(voucher.code, voucher.amount, voucher.usages).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("wrong data")))
  }

  def getVoucherById(id: Long): Action[AnyContent] = Action.async {
    val voucher = VoucherRepository.getByIdOption(id)
    voucher.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find given id")
    }
  }

  def getVoucherByCode(code: String): Action[AnyContent] = Action.async {
    val voucher = VoucherRepository.getByCodeOption(code)
    voucher.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("cant find")
    }
  }

  def listVouchers(): Action[AnyContent] = Action.async {
    val vouchers = VoucherRepository.list()
    vouchers.map { vouchers =>
      Ok(Json.toJson(vouchers))
    }
  }

  def updateVoucher(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Voucher].map {
      voucher =>
        VoucherRepository.update(voucher.id, voucher).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteVoucher(id: Long): Action[AnyContent] = Action.async {
    VoucherRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
