package com.fitbit.model.activities

sealed trait TimeSeriesType {
  def name: String = getClass.getSimpleName.toLowerCase()
  def endpoint: String
  override def toString = endpoint
}

case object Calories extends TimeSeriesType {
  val endpoint = "/activities/calories"
}

case object Distance extends TimeSeriesType {
  val endpoint = "/activities/distance"
}

case object Elevation extends TimeSeriesType {
  val endpoint = "/activities/elevation"
}

case object Floors extends TimeSeriesType {
  val endpoint = "/activities/floors"
}

case object Steps extends TimeSeriesType {
  val endpoint = "/activities/steps"
}

case object Heart extends TimeSeriesType {
  val endpoint = "/activities/heart"
}

object TimeSeriesType {
  def typeForName(name: String):TimeSeriesType = {
    name match {
      case "calories" => Calories
      case "distance" => Distance
      case "elevation" => Elevation
      case "floors" => Floors
      case "steps" => Steps
      case "heart" => Heart
    }
  }
}
