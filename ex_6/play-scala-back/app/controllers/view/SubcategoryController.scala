package controllers.view

import models.{Category, Subcategory}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{CategoryRepository, SubcategoryRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class SubcategoryController @Inject()(SubcategoryRepository: SubcategoryRepository, CategoryRepository: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val subcategoryForm: Form[CreateSubcategoryForm] = Form {
    mapping(
      "categoryId" -> longNumber,
      "name" -> nonEmptyText,
    )(CreateSubcategoryForm.apply)(CreateSubcategoryForm.unapply)
  }

  val updateSubcategoryForm: Form[UpdateSubcategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "categoryId" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateSubcategoryForm.apply)(UpdateSubcategoryForm.unapply)
  }

  def fetchData(): Unit = {
    CategoryRepository.list().onComplete {
      case Success(category) => categoryList = category
      case Failure(e) => print("error while listing categories", e)
    }
  }

  var categoryList: Seq[Category] = Seq[Category]()

  fetchData()

  def createSubcategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = CategoryRepository.list()

    categories.map(categories => Ok(views.html.subcategory_create(subcategoryForm, categories)))
  }

  def createSubcategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    subcategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.subcategory_create(errorForm, categoryList))
        )
      },
      subcategory => {
        SubcategoryRepository.create(subcategory.name, subcategory.categoryId).map { _ =>
          Redirect("/form/subcategory/list")
        }
      }
    )
  }

  def listSubcategories: Action[AnyContent] = Action.async { implicit request =>
    SubcategoryRepository.list().map(subcategories => Ok(views.html.subcategory_list(subcategories)))
  }

  def updateSubcategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val subcategory = SubcategoryRepository.getByIdOption(id)
    subcategory.map(subcategory => {
      val prodForm = updateSubcategoryForm.fill(UpdateSubcategoryForm(subcategory.get.id, subcategory.get.categoryId, subcategory.get.name))
      Ok(views.html.subcategory_update(prodForm, categoryList))
    })
  }

  def updateSubcategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateSubcategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.subcategory_update(errorForm, categoryList))
        )
      },
      subcategory => {
        SubcategoryRepository.update(subcategory.id, Subcategory(subcategory.id, subcategory.categoryId, subcategory.name)).map { _ =>
          Redirect("/form/subcategory/list")
        }
      }
    )
  }

  def deleteSubcategory(id: Long): Action[AnyContent] = Action {
    SubcategoryRepository.delete(id)
    Redirect("/form/subcategory/list")
  }
}

case class CreateSubcategoryForm(categoryId: Long, name: String)

case class UpdateSubcategoryForm(id: Long, categoryId: Long, name: String)
