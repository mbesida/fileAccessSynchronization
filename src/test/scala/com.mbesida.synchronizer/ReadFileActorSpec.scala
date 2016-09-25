package com.mbesida.synchronizer

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.mbesida.synchronizer.ReadFileActor.{ReadResult, ReadValue}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, WordSpecLike}

@RunWith(classOf[JUnitRunner])
class ReadFileActorSpec extends TestKit(ActorSystem("test"))
  with Matchers
  with WordSpecLike
  with ImplicitSender {

  "ReadActor" should {
    "be able to read csv file" in {
      val actor = system.actorOf(Props(classOf[ReadFileActor], "src/test/resources/f2.csv"))
      actor ! ReadValue(2)
      expectMsg(ReadResult(Right(2)))
      actor ! ReadValue(0)
      expectMsg(ReadResult(Right(23.45)))
    }
    "tell that value not found in case of out of bound" in {
      val actor = system.actorOf(Props(classOf[ReadFileActor], "src/test/resources/f2.csv"))
      actor ! ReadValue(20)
      expectMsg(ReadResult(Left("Not found data at index 20")))
    }
  }



}
