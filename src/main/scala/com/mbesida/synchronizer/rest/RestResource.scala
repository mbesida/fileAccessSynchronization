package com.mbesida.synchronizer.rest

import java.util.concurrent.TimeUnit
import javax.ws.rs._
import javax.ws.rs.container.{AsyncResponse, Suspended}
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.{MediaType, Response}

import akka.pattern._
import akka.util.Timeout
import com.mbesida.synchronizer.ServiceActor.{Get, Put}
import com.mbesida.synchronizer.cake.{ConfigProvider, ServiceProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@Path("/service")
class RestResource extends ServiceProvider with ConfigProvider {

  implicit val requestTimeout: Timeout = conf.getDuration("timeout", TimeUnit.MILLISECONDS)

  @GET
  @Path("/{v1}")
  @Produces(Array(MediaType.APPLICATION_XML))
  def asyncGet(@PathParam("v1") v1: Int, @Suspended asyncResponse: AsyncResponse): Unit = {
    asyncResponse.setTimeout(requestTimeout.duration.toSeconds, TimeUnit.SECONDS)
    (service ? Get(v1)).mapTo[Option[Double]] onComplete {
      case Success(Some(r)) =>
        asyncResponse.resume(GetResult(r))
      case Success(None) =>
        asyncResponse.resume(Response.status(Status.NOT_FOUND).build)
      case Failure(e) => asyncResponse.resume(Response.status(Status.INTERNAL_SERVER_ERROR).build)
    }
  }

  @POST
  @Consumes(Array(MediaType.APPLICATION_XML))
  @Produces(Array(MediaType.APPLICATION_XML))
  def asyncPost(input: InputData, @Suspended asyncResponse: AsyncResponse): Unit = {
    asyncResponse.setTimeout(requestTimeout.duration.toSeconds, TimeUnit.SECONDS)
    (service ? Put(input.v2, input.v3, input.v4)).mapTo[Int] onComplete {
      case Success(v) =>
        asyncResponse.resume(PostResult(v))
      case Failure(e) =>
        asyncResponse.resume(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build)
    }
  }

}
