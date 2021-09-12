package models

import play.api.libs.json.{Json, OFormat}

case class ProductOrder(id: Long, orderID: Long, productID: Long, amount: Double)

object ProductOrder {
  implicit val basketFormat: OFormat[ProductOrder] = Json.format[ProductOrder]
}
