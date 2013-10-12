import unfiltered.request._
import unfiltered.response._
import unfiltered.scalate._
import unfiltered.jetty.Http
import java.io._
import java.net._
import sys.process._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{ Try, Success, Failure }
import ExecutionContext.Implicits.global
import geocodr.github.search._
import geocodr.github.user._
import argonaut._
import Argonaut._
import argonaut.integrate.unfiltered._

object ServerPlan extends unfiltered.filter.Plan {
  def intent = {
    case req @ (GET(Path("/app")) | GET(Path("/"))) =>
      Ok ~> Scalate(req, "app.mustache")

    case req @ (GET(Path("/graph"))) =>
      Ok ~> Scalate(req, "graph.mustache")

    // GET /users?username=<name>
    case req @ (GET(Path("/users"))) =>
      req match {
        case Params(params) =>
          Ok ~> Scalate(req, "user.mustache", "username" -> params("username"))
      }

    case req @ GET(Path(Seg("users" :: "info" :: username :: Nil))) =>
      val search = Users.search(QueryText(username), Users.SortedByFollowers, Asc)
      val userInfo = for {
        Some(userSearch) <- search
        user <- userSearch.head.user
      } yield user.map(_.info)
      Ok ~> JsonResponse(Await.result(userInfo, 1 second).get)

    case req @ (GET(_)) =>
      Ok ~> Scalate(req, "404.mustache")
  }
}

object Geocodr {
  def main(args: Array[String]) {
    val watch = future { "sass --watch web/scss:web/css".!! }
    val server = Http.local(8080).context("/assets") {
      _.resources(new URL(s"file://${System.getProperty("user.dir")}/web"))
    }.filter(ServerPlan)
    server.run()
  }
}
