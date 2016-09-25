package com.mbesida.synchronizer.cake

import akka.actor.ActorSystem


trait ActorSystemProvider {
  implicit def actorSystem = ActorSystemProvider.system
}

object ActorSystemProvider {
  lazy val system = ActorSystem("fileAccessSynchronization")
}
