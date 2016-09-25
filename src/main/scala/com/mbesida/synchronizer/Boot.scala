package com.mbesida.synchronizer

import java.net.URI
import com.mbesida.synchronizer.cake.ConfigProvider
import com.mbesida.synchronizer.rest.RestResource
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig


object Boot extends App with ConfigProvider {
  val server = GrizzlyHttpServerFactory.createHttpServer(
    URI.create(s"http://${conf.getString("host")}:${conf.getInt("port")}"),
    new ResourceConfig(classOf[RestResource])
  )

  System.in.read()
  server.shutdown()
}
