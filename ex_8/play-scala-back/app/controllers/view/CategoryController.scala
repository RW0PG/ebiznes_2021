package controllers.view

import models.Category
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.CategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(CategoryRepository: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def createCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category_create(errorForm))
        )
      },
      category => {
        CategoryRepository.create(category.name).map { _ =>
          Redirect("/form/category/list")
        }
      }
    )
  }

  def listCategories: Action[AnyContent] = Action.async { implicit request =>
    CategoryRepository.list().map(categories => Ok(views.html.category_list(categories)))
  }

  def createCategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = CategoryRepository.list()
    categories.map(_ => Ok(views.html.category_create(categoryForm)))
  }

  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val category = CategoryRepository.getByIdOption(id)
    category.map(category => {
      val prodForm = updateCategoryForm.fill(UpdateCategoryForm(category.get.id, category.get.name))
      Ok(views.html.category_update(prodForm))
    })
  }

  def updateCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category_update(errorForm))
        )
      },
      category => {
        CategoryRepository.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect("/form/category/list")
        }
      }
    )
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action {
    CategoryRepository.delete(id)
    Redirect("/form/category/list")
  }
}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(id: Long, name: String)
