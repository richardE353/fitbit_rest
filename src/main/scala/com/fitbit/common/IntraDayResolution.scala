package com.fitbit.common

sealed trait IntraDayResolution {
  def pathValue: String
  override def toString = pathValue
}

case object Second extends IntraDayResolution {
  val pathValue = "1sec"
}

case object Minute extends IntraDayResolution {
  val pathValue = "1min"
}

case object QuarterHour extends IntraDayResolution {
  val pathValue = "15min"
}
