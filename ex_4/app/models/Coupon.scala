package models

import play.api.libs.json.{Json, OFormat}

case class Coupon(id: Long, value: Double, couponsUsages: Int)

object Coupon {
  implicit val couponsFormat: OFormat[Coupon] = Json.format[Coupon]
}
