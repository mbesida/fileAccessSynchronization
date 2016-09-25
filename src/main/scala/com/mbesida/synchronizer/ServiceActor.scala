package com.mbesida.synchronizer

import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.util.Locale

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import ReadFileActor.{ReadResult, ReadValue}
import ReadWriteFileActor.{ModifyFile, SuccessModification}
import com.mbesida.synchronizer.ReadWriteFileActor.SuccessModification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.Success


object ServiceActor {
  case class Get(v1: Int)
  case class Put(v2: Double, v3: Int, v4: Int)

  def props(f1Path: String, f2Path: String, timeout: Timeout) = Props(new ServiceActor(f1Path, f2Path, timeout))
}

class ServiceActor(f1Path: String, f2Path: String, timeout: Timeout) extends Actor {

  import ServiceActor._

  implicit val defaultTimeout = timeout

  val f1 = context.actorOf(Props(classOf[ReadFileActor], f1Path))
  val f2 = context.actorOf(Props(classOf[ReadWriteFileActor], f2Path))

  override def receive: Receive = {
    case Get(v1) => {
      val sendTo = sender
        fetchValueFrom(f2, v1){value =>
          sendTo ! Some(roundDouble(if (value > 10) value - 10 else value))
        }(sendTo ! None)
    }
    case Put(v2, v3, v4) => {
      val sendTo = sender
      fetchValueFrom(f1, v3) { value =>
        val a = value + v2
        (f2 ? ModifyFile(v4, roundDouble(a + (if (a < 10) 10 else 0)))).onComplete {
          case Success(SuccessModification) => sendTo ! 0
          case _ => sendTo ! 1
        }
      }(sendTo ! 1)
    }

  }

  def fetchValueFrom(f: ActorRef, index: Int)(successFunc: Double => Unit)(failureFunc: => Unit): Future[Unit] = {
    (for {
      result <- (f ? ReadValue(index)).mapTo[ReadResult]
      v <- future(result.res.right.toOption.get)
    } yield successFunc(v)).recover{case _ => failureFunc}
  }

  def roundDouble(v: Double): Double = {
    val formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US))
    val str = formatter.format(v)
    formatter.parse(str).doubleValue()
  }
}

