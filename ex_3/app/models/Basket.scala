package models

import play.api.libs.json.{Json, OFormat}

case class Basket(id: Long, orderID: Long, productID: Long, total: Double)

object Basket {
  implicit val basketFormat: OFormat[Basket] = Json.format[Basket]
}
