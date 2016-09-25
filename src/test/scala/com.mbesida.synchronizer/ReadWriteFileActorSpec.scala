package com.mbesida.synchronizer

import java.io.PrintWriter

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import au.com.bytecode.opencsv.CSVWriter
import com.mbesida.synchronizer.ReadFileActor.{ReadResult, ReadValue}
import com.mbesida.synchronizer.ReadWriteFileActor.{FailedModification, ModifyFile, SuccessModification}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.reflect.io.Path

@RunWith(classOf[JUnitRunner])
class ReadWriteFileActorSpec extends TestKit(ActorSystem("test"))
  with ImplicitSender
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {


  override def beforeAll(): Unit = {
    val out = new CSVWriter(new PrintWriter("sampleFile.csv"))
    out.writeNext(Array("1.45", "2", "0.45"))
    out.close()
  }

  "ReadWriteFileActor" should {
    "modify empty file success scenario" in {
      val actor = system.actorOf(Props(classOf[ReadWriteFileActor], "samplef2.csv"))
      actor ! ModifyFile(0, 2.4)
      expectMsg(SuccessModification)
    }
    "modify empty file failure scenario" in {
      val actor = system.actorOf(Props(classOf[ReadWriteFileActor], "samplef2.csv"))
      actor ! ModifyFile(10, 20)
      expectMsg(FailedModification)
    }
    "modify non empty file" in {
      val actor = system.actorOf(Props(classOf[ReadWriteFileActor], "sampleFile.csv"))
      actor ! ReadValue(2)
      expectMsg(ReadResult(Right(0.45)))
      actor ! ModifyFile(1, 3.14)
      expectMsg(SuccessModification)
      actor ! ReadValue(1)
      expectMsg(ReadResult(Right(3.14)))
    }
  }


  override def afterAll(): Unit = {
    Path("samplef2.csv").deleteIfExists()
    Path("sampleFile.csv").deleteIfExists()
  }

}
