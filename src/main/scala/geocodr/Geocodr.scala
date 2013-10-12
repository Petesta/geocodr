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
  val response = 
    <html>
      <head>
        <link href="assets/css/core.css" media="screen" type="text/css" rel="stylesheet"></link>
      </head>
      <body>
        Hello World!
      </body>
    </html>

  def intent = {
    case req @ GET(_) => Ok ~> Scalate(req, "helloWorld.ssp") 
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

