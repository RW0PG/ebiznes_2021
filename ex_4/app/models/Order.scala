package models

import play.api.libs.json.{Json, OFormat}

case class Order(id: Long, userID: Long, paymentID: Long, CouponID: Long)

object Order {
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}
