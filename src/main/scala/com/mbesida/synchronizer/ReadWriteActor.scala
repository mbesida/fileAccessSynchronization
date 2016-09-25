package com.mbesida.synchronizer

import java.io.PrintWriter
import java.text.{DecimalFormatSymbols, DecimalFormat}
import java.util.Locale

import au.com.bytecode.opencsv.CSVWriter

import scala.util.Try

object ReadWriteFileActor {
  case class ModifyFile(index: Int, value: Double)
  case object SuccessModification
  case object FailedModification
}

class ReadWriteFileActor(fileName: String) extends ReadFileActor(fileName) {
  import ReadWriteFileActor._
  override def receive: Receive = super.receive orElse {
    case ModifyFile(index, newValue) => Try {
      val data = readFile(fileName).getOrElse(Array.empty)
      if (index < data.size) {
        writeData(data.updated(index, newValue))
        sender ! SuccessModification
      } else if (index == data.size) { //add new element to file
        writeData(data :+ newValue)
        sender ! SuccessModification
      } else sender ! FailedModification
    } recover{case _ => sender ! FailedModification}
  }

  def writeData(data: Array[Double]): Unit = {
    val out = new CSVWriter(new PrintWriter(fileName))
    out.writeNext(data.map(_.toString))
    out.close
  }
}
