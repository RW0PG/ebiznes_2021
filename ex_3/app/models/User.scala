package models

import play.api.libs.json.{Json, OFormat}
import sun.security.util.Password

case class User(id: Long, name: String, email: String, password: Password)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}