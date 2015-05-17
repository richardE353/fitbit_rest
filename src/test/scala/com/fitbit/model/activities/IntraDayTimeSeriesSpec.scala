package com.fitbit.model.activities

import org.scalatest.{FunSpec, Matchers}
import spray.json._

import scala.io.Source


class IntraDayTimeSeriesSpec extends FunSpec with Matchers with IntraDayTimeSeriesJsonProtocol {
  describe("Deserialization") {
    it("can parse calories") {
      val bufferedSource = Source.fromURL(getClass.getResource("/calories.json"))
      val inputData = bufferedSource.getLines.mkString
      val testData = inputData.parseJson

      val convertedVal = testData.convertTo[IntraDayTimeSeries]

      println("converted: " + convertedVal.toString)

      val seriesJson = convertedVal.toJson
      println("json: " + seriesJson.compactPrint)
    }

    it("can parse steps") {
      val bufferedSource = Source.fromURL(getClass.getResource("/steps.json"))
      val inputData = bufferedSource.getLines.mkString
      val testData = inputData.parseJson

      val convertedVal = testData.convertTo[IntraDayTimeSeries]

      println("converted: " + convertedVal.toString)
    }
  }
}
