package models

import play.api.libs.json.{Json, OFormat}

case class ProductInsStock(id: Long, productID: Long, amount: Int, price: Double)

object ProductInsStock {
  implicit val productInStockFormat: OFormat[ProductInsStock] = Json.format[ProductInsStock]
}
