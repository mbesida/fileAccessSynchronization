package com.mbesida.synchronizer

import java.io.PrintWriter
import java.net.URI
import javax.ws.rs.client.{Entity, ClientBuilder}

import au.com.bytecode.opencsv.CSVWriter
import com.mbesida.synchronizer.cake.ConfigProvider
import com.mbesida.synchronizer.rest.{RestResource, PostResult, InputData, GetResult}
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.reflect.io.Path

@RunWith(classOf[JUnitRunner])
class RestSpec extends WordSpecLike with Matchers with BeforeAndAfterAll with ConfigProvider {

  val server = GrizzlyHttpServerFactory.createHttpServer(
    URI.create(s"http://${conf.getString("host")}:${conf.getInt("port")}"),
    new ResourceConfig(classOf[RestResource]))

  override protected def beforeAll(): Unit = {
    val f1 = new CSVWriter(new PrintWriter("f1.csv"))
    val f2 = new CSVWriter(new PrintWriter("f2.csv"))
    f1.writeNext(Array("1", "16.01"))
    f2.writeNext(Array("10", "23.5", "0.45"))
    f1.close(); f2.close()
  }

  val webTarget = ClientBuilder.newClient().target("http://localhost:8080/service")

  "Rest" should {
    "be able to perform GET request and extract response" in {
      var response = webTarget.path("/{id}").resolveTemplate("id", 2).request().async().get().get()
      response.readEntity(classOf[GetResult]) should be(GetResult(0.45))
      response = webTarget.path("/{id}").resolveTemplate("id", 0).request().async().get().get()
      response.readEntity(classOf[GetResult]) should be(GetResult(10))
    }
    "be able to perform POST and successfuly modify file" in {
      val postResponse = webTarget.request().async().post(Entity.xml(InputData(0.35, 1, 0))).get()
      postResponse.readEntity(classOf[PostResult]) should be(PostResult(0))
      var getResponse = webTarget.path("/{id}").resolveTemplate("id", 0).request().async().get().get()
      getResponse.readEntity(classOf[GetResult]) should be(GetResult(6.36))
      getResponse = webTarget.path("/{id}").resolveTemplate("id", 1).request().async().get().get()
      getResponse.readEntity(classOf[GetResult]) should be(GetResult(13.5))
    }

    "be able to perform POST and failed to modify file" in {
      val postResponse = webTarget.request().async().post(Entity.xml(InputData(0.35, 10, 0))).get()
      postResponse.readEntity(classOf[PostResult]) should be(PostResult(1))
      val getResponse = webTarget.path("/{id}").resolveTemplate("id", 0).request().async().get().get()
      getResponse.readEntity(classOf[GetResult]) should be(GetResult(6.36))
    }
  }

  override protected def afterAll(): Unit = {
    Path("f1.csv").deleteIfExists()
    Path("f2.csv").deleteIfExists()
    server.shutdown()
  }
}
