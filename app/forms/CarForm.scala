package forms

import models.Car
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.Reads.pattern
import play.api.libs.json.{JsValue, Json, Reads, Writes, __}

object CarForm {
  implicit val carWrites: Writes[Car] = (
      (__ \ "number").write[String] and
      (__ \ "kind").write[Option[String]] and
      (__ \ "model").write[Option[String]] and
      (__ \ "color").write[Option[String]] and
      (__ \ "year").write[Option[Int]]
    )(unlift(Car.unapply))

  implicit val carReads: Reads[Car] = (
    (__ \ "number").read[String](pattern("\\w\\d{3}\\w{2}\\d{2,3}".r, "Car number pattern error")) and
    (__ \  "kind").readNullable[String] and
    (__ \  "model").readNullable[String] and
    (__ \  "color").readNullable[String] and
    (__ \ "year").readNullable[Int]
  )(Car.apply _)

  def getJsonifiedMap(map: Map[String, Any]): Map[String, JsValue] = {
    map.map(pair => {
      val (key, value) = pair
      value match {
        case num: Int => key -> Json.toJson(num)
        case str: String => key -> Json.toJson(str)
        case map: Map[String, Any] => key -> Json.toJson(getJsonifiedMap(map))
        case _ => key -> Json.toJson("unknown type")
      }
    })
  }
}
