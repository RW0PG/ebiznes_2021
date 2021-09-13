package models

import play.api.libs.json._


case class User(id: Long = 0, email: String, nickname: String, password: String)

object User {
  implicit val userFormat: OFormat[User] = Json.using[Json.WithDefaultValues].format[User]
}
