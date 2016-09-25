package com.mbesida.synchronizer

import java.io.{FileReader, BufferedReader}

import akka.actor.Actor
import au.com.bytecode.opencsv.CSVReader
import scala.collection.JavaConversions._

import scala.util.Try

object ReadFileActor {
  case class ReadValue(index: Int)
  case class ReadResult(res: Either[String, Double])
}

class ReadFileActor(fileName: String) extends Actor {
  import ReadFileActor._

  override def receive: Receive = {
    case ReadValue(index) => readFile(fileName) map { fileContent =>
      if (index < fileContent.size) sender ! ReadResult(Right(fileContent(index)))
      else sender ! ReadResult(Left(s"Not found data at index $index"))
    } recover{case e => sender ! ReadResult(Left(e.getMessage))}
  }

  def readFile(fileName: String): Try[Array[Double]] = Try {
    val r = new CSVReader(new BufferedReader(new FileReader(fileName)))
    val allContent = r.readAll().map(line => line.map(_.toDouble))
    val result = if (allContent.nonEmpty) allContent.head else Array.empty[Double]
    r.close()
    result
  }
}
