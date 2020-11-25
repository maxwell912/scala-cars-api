package controllers

import forms.CarForm._
import javax.inject._
import models.{Car, CarDAO}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsPath, Json, JsonValidationError}
import play.api.mvc._

import scala.collection.Seq
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(carService: CarDAO, val controllerComponents: ControllerComponents)
                              (implicit executionContext: ExecutionContext)
  extends BaseController {

  def all: Action[AnyContent] = Action.async {
    for {
      carSeq <- carService.all
    } yield Ok(
      Json.toJson(
        carSeq.map(Json.toJson(_)(carWrites))
      )
    )

  }

  def insert: Action[AnyContent] = Action.async { implicit request =>
    val requestBody = request.body.asJson

    val response = requestBody.map(json => {
      json.validate[Car](carReads)
        .fold(
          validationErrors => Future.successful(
            BadRequest(validationErrorsToJson(validationErrors))
          ),
          car => carService.insert(car).map {
            case Right(value) => Ok(value)
            case Left(error) => BadRequest(error)
          }
        )
    })

    response match {
      case None => Future.successful(BadRequest("No data to insert"))
      case Some(resp) => resp
    }
  }

  private def validationErrorsToJson(validationErrors: Seq[(JsPath, Seq[JsonValidationError])]) = {
    Json.toJson(
      for {
        (path, errors) <- validationErrors
        pathStr = path.toJsonString
        errorArr = errors.map(er => er.message).toArray
      } yield s"$pathStr: ${errorArr.mkString(", ")}"
    )
  }

  def delete(carNumber: String): Action[AnyContent] = Action.async {
    carService.delete(carNumber).map({
      case Right(value) => Ok(value)
      case Left(error) => BadRequest(error)
    })
  }

  def getStatistic: Action[AnyContent] = Action.async {
    for {
      statistic <- carService.getStatistic
    } yield Ok(
      Json.toJson(
        getJsonifiedMap(statistic)
      )
    )
  }
}
