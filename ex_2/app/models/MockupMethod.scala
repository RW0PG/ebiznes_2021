package models

import play.api.libs.json._

case class MockupMethod(id: String, name: String)

object MockupMethod {
  implicit val format: OFormat[MockupMethod] = Json.format[MockupMethod]
}