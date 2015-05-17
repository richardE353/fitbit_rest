package com.fitbit.common

sealed trait Period {
  def name: String
  override def toString = name
}

case object OneDay extends Period {
  val name = "1d"
}

case object SevenDay extends Period {
  val name = "7d"
}

case object ThirtyDay extends Period {
  val name = "30d"
}

case object Week extends Period {
  val name = "1w"
}

case object Month extends Period {
  val name = "1m"
}

case object QuarterYear extends Period {
  val name = "3m"
}

case object HalfYear extends Period {
  val name = "6m"
}

case object Year extends Period {
  val name = "1y"
}
