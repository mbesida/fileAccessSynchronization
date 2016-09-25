package com.mbesida.synchronizer.cake

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.util.Timeout
import com.mbesida.synchronizer.ServiceActor

trait ServiceProvider {
  def service: ActorRef = ServiceProvider.serviceActor
}

object ServiceProvider extends ActorSystemProvider with ConfigProvider {

  lazy val serviceActor = actorSystem.actorOf(
    ServiceActor.props(
      conf.getString("f1.path"),
      conf.getString("f2.path"),
      Timeout(conf.getDuration("timeout", TimeUnit.SECONDS), TimeUnit.SECONDS))
  )
}
