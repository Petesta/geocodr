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
import geocodr.github.repository._
import argonaut._
import Argonaut._
import argonaut.integrate.unfiltered._

//case class Error(msg: String)
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
      val search = Users.search(QueryText(username) + SearchIn("login"), Users.SortedByFollowers, Desc)
      val userInfo = for {
        Some(userSearch) <- search
        user <- userSearch.filter(_.login.toLowerCase == username.toLowerCase).head.user
      } yield user.map(_.info(center = true))
      Ok ~> JsonResponse(Await.result(userInfo, 10 seconds).get)

    case req @ GET(Path(Seg("users" :: "languages" :: username :: Nil))) =>
      val search = Users.search(QueryText(username) + SearchIn("login"), Users.SortedByFollowers, Desc)
      val langs = for {
        Some(userSearch) <- search
        user <- userSearch.filter(_.login.toLowerCase == username.toLowerCase).head.user
        languages <- user.map(_.languages).getOrElse(Future.successful { Map.empty })
      } yield languages
      Ok ~> ResponseString(Await.result(langs, 10 seconds).map {
        case (k, v) => s"""{ "language": "$k", "percent": $v }"""
      }.mkString( "[", "," , "]"))

    case req @ GET(Path(Seg("users" :: "starred" :: uname1 :: uname2 :: Nil))) =>
      val search1 = Users.search(QueryText(uname1), Users.SortedByFollowers, Desc)
      val search2 = Users.search(QueryText(uname2), Users.SortedByFollowers, Desc)
      val repos = for {
        Some(userSearch1) <- search1
        Some(userSearch2) <- search2
        Some(user1) <- userSearch1.filter(_.login.toLowerCase == uname1.toLowerCase).head.user
        Some(user2) <- userSearch2.filter(_.login.toLowerCase == uname2.toLowerCase).head.user
        starred1 <- user1.starredRepos
        starred2 <- user2.starredRepos
      } yield Repository.intersect(starred1, starred2)
      Ok ~> JsonResponse(Await.result(repos, 10 seconds).toList)


    case req @ (GET(_)) =>
      Ok ~> Scalate(req, "404.mustache")
  }
}

object Geocodr {
  def main(args: Array[String]) {
    val watch = future { "sass --watch web/scss:web/css".!! }
    val server = Http(args[1]).context("/assets") {
      _.resources(new URL(s"file://${System.getProperty("user.dir")}/web"))
    }.filter(ServerPlan)
    server.run()
  }
}
