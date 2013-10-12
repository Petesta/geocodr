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
        <meta name="viewport" content="width=device-width" />
        <link href="http://fonts.googleapis.com/css?family=Open+Sans:300" rel="stylesheet" type="text/css" />
        <link href="assets/css/core.css" media="screen" type="text/css" rel="stylesheet" />
      </head>
      <body>
        <div class="container">
          <div class="landing-container">
            <div class="cta-container">
              <h1>geocodr</h1>
              <p>Connect with developers like you in your area.</p>
            </div>

            <div class="login-container">
              <form action="">
                <input type="text" placeholder="Enter your GitHub username"/>
                <input type="submit" class="btn btn-large btn-blue" value="Go"/>
              </form>
            </div>
          </div>
        </div>
      </body>
    </html>

  def intent = {
    case req @ GET(Path("/login")) | GET(Path("/")) =>
      Ok ~> Scalate(req, "login.ssp")
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

