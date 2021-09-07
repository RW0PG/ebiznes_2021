package models

import play.api.libs.json.{Json, OFormat}

case class UserCredentials(id: Long, userID: Long, firstName: String, lastName: String, street: String, zipcode: String, city: String, country: String)

object UserCredentials {
  implicit val userCredentialsFormat: OFormat[UserCredentials] =  Json.format[UserCredentials]
}
