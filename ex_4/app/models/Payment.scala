package models

import play.api.libs.json.{Json, OFormat}

case class Payment(id: Long, userID: Long, paymentCardID: Long, total: Double)

object Payment {
  implicit val paymentFormat: OFormat[Payment] = Json.format[Payment]
}
