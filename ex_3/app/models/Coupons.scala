package models

import play.api.libs.json.{Json, OFormat}

case class Coupons(id: Long, value: Double)

object Coupons {
  implicit val couponsFormat: OFormat[Coupons] = Json.format[Coupons]
}
