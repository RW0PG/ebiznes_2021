package models

import play.api.libs.json.{Json, OFormat}

case class ProductInStock(id: Long, amount: Int, totalValue: Double, singleValue: Double)

object ProductInStock {
  implicit val productInStockFormat: OFormat[ProductInStock] = Json.format[ProductInStock]
}
