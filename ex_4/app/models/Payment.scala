package models

import play.api.libs.json.{Json, OFormat}

case class Payment(id: Long, userID: Long, total: Double, paymentType: String)

object Payment {
  implicit val paymentFormat: OFormat[Payment] = Json.format[Payment]
}
