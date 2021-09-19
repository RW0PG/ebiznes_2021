package controllers.view

import models.{CreditCard, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{CreditCardRepository, UserRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CreditCardController @Inject()(UserRepository: UserRepository, CreditCardRepository: CreditCardRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val url = "/form/credit-card/list"

  val creditCardForm: Form[CreateCreditCardForm] = Form {
    mapping(
      "userId" -> longNumber,
      "cardholderName" -> nonEmptyText,
      "number" -> nonEmptyText,
      "expDate" -> nonEmptyText,
      "cvcCode" -> nonEmptyText,
    )(CreateCreditCardForm.apply)(CreateCreditCardForm.unapply)
  }

  val updateCreditCardForm: Form[UpdateCreditCardForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "cardholderName" -> nonEmptyText,
      "number" -> nonEmptyText,
      "expDate" -> nonEmptyText,
      "cvcCode" -> nonEmptyText,
    )(UpdateCreditCardForm.apply)(UpdateCreditCardForm.unapply)
  }

  def fetchData(): Unit = {
    UserRepository.list().onComplete {
      case Success(user) => userList = user
      case Failure(e) => print("error while listing users", e)
    }
  }

  var userList: Seq[User] = Seq[User]()

  fetchData()

  def listCreditCards: Action[AnyContent] = Action.async { implicit request =>
    CreditCardRepository.list().map(creditCards => Ok(views.html.credit_card_list(creditCards)))
  }

  def createCreditCard(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = UserRepository.list()

    users.map(users => Ok(views.html.credit_card_create(creditCardForm, users)))
  }

  def createCreditCardHandle(): Action[AnyContent] = Action.async { implicit request =>
    creditCardForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.credit_card_create(errorForm, userList))
        )
      },
      creditCard => {
        CreditCardRepository.create(creditCard.userId, creditCard.cardholderName, creditCard.number, creditCard.expDate, creditCard.cvcCode).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def updateCreditCard(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val creditCard = CreditCardRepository.getByIdOption(id)
    creditCard.map(creditCard => {
      val prodForm = updateCreditCardForm.fill(UpdateCreditCardForm(creditCard.get.id, creditCard.get.userId, creditCard.get.cardholderName, creditCard.get.number, creditCard.get.expDate, creditCard.get.cvcCode))
      Ok(views.html.credit_card_update(prodForm, userList))
    })
  }

  def updateCreditCardHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCreditCardForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.credit_card_update(errorForm, userList))
        )
      },
      creditCard => {
        CreditCardRepository.update(creditCard.id, CreditCard(creditCard.id, creditCard.userId, creditCard.cardholderName, creditCard.number, creditCard.expDate, creditCard.cvcCode)).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def deleteCreditCard(id: Long): Action[AnyContent] = Action {
    CreditCardRepository.delete(id)
    Redirect(url)
  }
}

case class CreateCreditCardForm(userId: Long, cardholderName: String, number: String, expDate: String, cvcCode: String)

case class UpdateCreditCardForm(id: Long, userId: Long, cardholderName: String, number: String, expDate: String, cvcCode: String)
