package models

import java.sql.Date
import play.api.libs.json.{Json, OFormat}
import sun.security.util.Password

case class User(id: Long, name: String, email: String, password: String)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}