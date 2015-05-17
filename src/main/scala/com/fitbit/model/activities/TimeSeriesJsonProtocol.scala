package com.fitbit.model.activities

import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}
import org.joda.time.{LocalDateTime, LocalTime}
import spray.json._


trait LocalTimeJsonProtocol extends DefaultJsonProtocol {
  implicit object LocalTimeJsonFormat extends JsonFormat[LocalTime] {
    val formatter = ISODateTimeFormat.hourMinuteSecond
    def write(dateTime: LocalTime) =  JsString(formatter.print(dateTime))

    def read(value: JsValue) = value match {
      case JsString(dt) => formatter.parseLocalTime(dt)
      case _ => deserializationError("LocalTime expected")
    }
  }
}

trait LocalDateTimeJsonProtocol extends DefaultJsonProtocol {
  implicit object LocalDateTimeJsonFormat extends JsonFormat[LocalDateTime] {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    def write(dateTime: LocalDateTime) = JsString(formatter.print(dateTime))

    def read(value: JsValue) = value match {
      case JsString(dt) => formatter.parseLocalDateTime(dt)
      case _ => deserializationError("LocalDateTime expected")
    }
  }
}

trait IntraDayTimeSeriesJsonProtocol extends DefaultJsonProtocol with LocalTimeJsonProtocol {
  implicit object TimeValueJsonFormat extends JsonFormat[TimeValue] {
    implicit val intTimeValueFormat = jsonFormat2(IntTimeValue)
    implicit val calorieDataValueFormat = jsonFormat(CalorieDataValue, "level", "mets", "time", "value")

    def write(obj: TimeValue): JsValue = obj match {
      case cObj: CalorieDataValue => calorieDataValueFormat.write(cObj)
      case tObj: IntTimeValue => intTimeValueFormat.write(tObj)
    }

    def read(json: JsValue): TimeValue = {

      val jsObject = json.asJsObject
      val keysLength =  jsObject.fields.keys.size
      keysLength match {
        case 2 => intTimeValueFormat.read(json)
        case _ => calorieDataValueFormat.read(json)
      }
    }
  }

  implicit object IntraDayTimeSeriesJsonFormat extends RootJsonFormat[IntraDayTimeSeries] with LocalDateTimeJsonProtocol {
    implicit val dateTimeValueFormat = jsonFormat2(DateTimeValue)
    implicit val resourceSummaryFormat = jsonFormat2(ResourceSummary)
    implicit val intraDayDataFormat = jsonFormat3(IntraDayData)

    def write(obj: IntraDayTimeSeries): JsValue = JsObject(
      "name" -> obj.seriesType.name.toJson,
      "summary" -> obj.summary.toJson,
      "data" -> obj.data.toJson
    )

    def read(value: JsValue) = {
      val jsObject = value.asJsObject
      val keys = jsObject.fields.keys.toSeq.sorted

      keys match {
        case Seq("activities-calories", "activities-calories-intraday") =>
          val activityValues = jsObject.getFields("activities-calories", "activities-calories-intraday")
          createIntraDayTimeSeries(Calories, activityValues)

        case Seq("activities-distance", "activities-distance-intraday") =>
          val activityValues = jsObject.getFields("activities-distance", "activities-distance-intraday")
          createIntraDayTimeSeries(Distance, activityValues)

        case Seq("activities-elevation", "activities-elevation-intraday") =>
          val activityValues = jsObject.getFields("activities-elevation", "activities-elevation-intraday")
          createIntraDayTimeSeries(Elevation, activityValues)

        case Seq("activities-floors", "activities-floors-intraday") =>
          val activityValues = jsObject.getFields("activities-floors", "activities-floors-intraday")
          createIntraDayTimeSeries(Floors, activityValues)

        case Seq("activities-heart", "activities-heart-intraday") =>
          val activityValues = jsObject.getFields("activities-heart", "activities-heart-intraday")
          createIntraDayTimeSeries(Floors, activityValues)

        case Seq("activities-steps", "activities-steps-intraday") =>
          val activityValues = jsObject.getFields("activities-steps", "activities-steps-intraday")
          createIntraDayTimeSeries(Steps, activityValues)

        case _ => throw new Exception("unsupported intra-day activity")
      }
    }

    def createIntraDayTimeSeries(seriesType: TimeSeriesType, activityFields: Seq[JsValue]): IntraDayTimeSeries = {
      val summary = activityFields.head.convertTo[Seq[ResourceSummary]].map(_.toDateTimeValue)
      val data = activityFields.last.convertTo[IntraDayData]

      IntraDayTimeSeries(seriesType, summary, data)
    }
  }
}

object IntraDayTimeSeriesProtocol extends spray.json.DefaultJsonProtocol with IntraDayTimeSeriesJsonProtocol with LocalDateTimeJsonProtocol {
  implicit val dateTimeValueFormat = jsonFormat2(DateTimeValue)
  implicit val intTimeValueFormat = jsonFormat2(IntTimeValue)
  implicit val calorieDataValueFormat = jsonFormat(CalorieDataValue, "level", "mets", "time", "value")
}

