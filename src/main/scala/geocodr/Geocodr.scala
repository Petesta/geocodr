import unfiltered.request._
import unfiltered.response._
import unfiltered.scalate._
import unfiltered.jetty.Http
import java.io._
import java.net._
import sys.process._
import scala.concurrent._
import ExecutionContext.Implicits.global

object HelloPlan extends unfiltered.filter.Plan {
  def intent = {
    case req @ (GET(Path("/app")) | GET(Path("/"))) =>
      Ok ~> Scalate(req, "app.ssp")

    case req @ (GET(Path(Seg("users" :: username :: Nil)))) =>
      Ok ~> Scalate(req, "user.ssp", "username" -> username)

    case req @ (GET(_)) =>
      Ok ~> Scalate(req, "404.ssp")
  }
}

object Geocodr {
  def main(args: Array[String]) {
    val watch = future { "sass --watch web/scss:web/css".!! }
    val server = Http.local(8080).context("/assets") {
      _.resources(new URL(s"file://${System.getProperty("user.dir")}/web"))
    }.filter(HelloPlan)
    server.run()
  }
}
