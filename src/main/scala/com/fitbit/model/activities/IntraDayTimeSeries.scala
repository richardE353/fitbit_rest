package com.fitbit.model.activities

import org.joda.time.{LocalDateTime, LocalTime}

case class IntraDayTimeSeries(seriesType: TimeSeriesType, summary: Seq[DateTimeValue], data: IntraDayData)

case class DateTimeValue(dateTime: LocalDateTime, value: Option[Int])

case class ResourceSummary(dateTime: LocalDateTime, value: String) {
  def toDateTimeValue: DateTimeValue = {
    try {
      val intVal = value.toInt
      DateTimeValue(dateTime, Some(intVal))

    } catch {
      case t:Throwable => DateTimeValue(dateTime, None)
    }
  }
}

case class IntraDayData(datasetInterval: Int, dataset: Seq[TimeValue], datasetType: Option[String])

abstract class TimeValue {
  def time: LocalTime
  def value: AnyVal
}

case class IntTimeValue(time: LocalTime, value: Int) extends TimeValue
case class CalorieDataValue(mets: Int, level: Int, time: LocalTime, value: Float) extends TimeValue
