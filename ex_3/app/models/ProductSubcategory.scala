package models

import play.api.libs.json.{Json, OFormat}

case class ProductSubcategory(id: Long, categoryID: Long, subcategoryName: String)

object ProductSubcategory {
  implicit val productSubcategoryFormat: OFormat[ProductSubcategory] = Json.format[ProductSubcategory]
}
