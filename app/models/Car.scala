package models

import javax.inject.Inject
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}


case class Car(number: String,
               kind: Option[String],
               model: Option[String],
               color: Option[String],
               year: Option[Int])


class CarDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val cars = TableQuery[CarsTable]

  def all: Future[Seq[Car]] = db.run(
    cars.sortBy(_.year).sortBy(_.model.nullsLast).result
  )

  def insert(car: Car): Future[Either[String, String]] = db.run ({
    (cars += car).map(_ => Right("Success"))
  }).recover {
    case exception: PSQLException => Left(
      exception.getServerErrorMessage.toString
    )
  }

  def delete(number: String): Future[Either[String, String]] = db.run {
    cars.filter(_.number === number)
      .delete
      .map({
        case 1 => Right("Success")
        case 0 => Left("Key not found")
      })
  }

  def getSize: Future[Int] = db.run(cars.length.result)

  def getCarModelCounter: Future[Seq[(String, Int)]] = db.run {
    cars
      .groupBy(c => c.model)
      .map(group => (group._1.getOrElse("unknown"), group._2.size))
      .result
  }

  def getStatistic: Future[Map[String, Any]] = {
    val statisticResult = mutable.Map[String, Any]()
    for {
      tableSize <- getSize
      groupSeq <- getCarModelCounter
    } yield {
      statisticResult.update("all", tableSize.toString)
      statisticResult.update("modelsCount", groupSeq.toMap)
      statisticResult.toMap
    }
  }

  private class CarsTable(tag: Tag) extends Table[Car](tag, "CARS") {
    def kind = column[Option[String]]("MACHINE_KIND")

    def model = column[Option[String]]("MODEL")

    def number = column[String]("NUMBER", O.PrimaryKey)

    def color = column[Option[String]]("COLOR")

    def year = column[Option[Int]]("PRODUCTION_YEAR")

    def * = (number, kind, model, color, year) <> (Car.tupled, Car.unapply)
  }
}