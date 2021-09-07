package models

import play.api.libs.json.{Json, OFormat}

case class Product(id: Long, name: String, categoryID: Long, subcategoryID: Long, descriptionID: Long, stockID: Long)

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
