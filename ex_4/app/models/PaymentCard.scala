package models

import play.api.libs.json.{Json, OFormat}

case class PaymentCard(id: Long, userID: Long, number: String, cardOwner: String)

object PaymentCard {
  implicit val paymentCardFormat: OFormat[PaymentCard] = Json.format[PaymentCard]
}
