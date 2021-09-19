package controllers.view

import models.Voucher
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.VoucherRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherController @Inject()(VoucherRepository: VoucherRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val url = "/form/voucher/list"

  val voucherForm: Form[CreateVoucherForm] = Form {
    mapping(
      "code" -> text,
      "amount" -> number,
      "usages" -> number,
    )(CreateVoucherForm.apply)(CreateVoucherForm.unapply)
  }

  val updateVoucherForm: Form[UpdateVoucherForm] = Form {
    mapping(
      "id" -> longNumber,
      "code" -> text,
      "amount" -> number,
      "usages" -> number,
    )(UpdateVoucherForm.apply)(UpdateVoucherForm.unapply)
  }

  def createVoucher(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val vouchers = VoucherRepository.list()
    vouchers.map(_ => Ok(views.html.voucher_create(voucherForm)))
  }

  def createVoucherHandle(): Action[AnyContent] = Action.async { implicit request =>
    voucherForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.voucher_create(errorForm))
        )
      },
      voucher => {
        VoucherRepository.create(voucher.code, voucher.amount, voucher.usages).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def listVouchers: Action[AnyContent] = Action.async { implicit request =>
    VoucherRepository.list().map(vouchers => Ok(views.html.voucher_list(vouchers)))
  }

  def updateVoucher(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val voucher = VoucherRepository.getByIdOption(id)
    voucher.map(voucher => {
      val prodForm = updateVoucherForm.fill(UpdateVoucherForm(voucher.get.id, voucher.get.code, voucher.get.amount, voucher.get.usages))
      Ok(views.html.voucher_update(prodForm))
    })
  }

  def updateVoucherHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateVoucherForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.voucher_update(errorForm))
        )
      },
      voucher => {
        VoucherRepository.update(voucher.id, Voucher(voucher.id, voucher.code, voucher.amount, voucher.usages)).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def deleteVoucher(id: Long): Action[AnyContent] = Action {
    VoucherRepository.delete(id)
    Redirect(url)
  }
}

case class CreateVoucherForm(code: String, amount: Int, usages: Int)

case class UpdateVoucherForm(id: Long, code: String, amount: Int, usages: Int)
