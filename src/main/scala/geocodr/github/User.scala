package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._
import geocodr.github.search._
import geocodr.github.repository._
import scalaz._
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.option._
import scala.concurrent.{ Future => _, _ }
import scala.concurrent.duration._
import scala.Exception

object user {
  implicit def EncodeUserListJson: EncodeJson[List[User]] =
    EncodeJson((us: List[User]) => jArray(us.map(u => UserInfoEncodeJson.encode(u.info(false)))))

  implicit def UserInfoEncodeJson =
    jencode4L((ui: UserInfo) => (ui.name, ui.avatarUrl, ui.location, ui.nearbyUsers))("name", "avatarUrl", "location", "nearbyUsers")

  case class UserInfo(name: String, avatarUrl: String, location: Option[String], nearbyUsers: List[User])

  case class User (
    login: String,
    id: Long,
    avatarUrl: String,
    gravatarId: String,
    url: String,
    name: Option[String],
    company: Option[String],
    blog: Option[String],
    location: Option[String],
    email: Option[String],
    hireable: Option[Boolean],
    bio: Option[String],
    publicRepos: Long,
    publicGists: Long,
    followers: Long,
    following: Long,
    htmlUrl: String,
    createdAt: String,
    accountType: String
  ) {

    def info(center: Boolean = false): UserInfo = {
      val nearby = if (!center) { List.empty } else { Await.result(localUsers, 10 seconds) }
      UserInfo(login, avatarUrl, location, nearby)
    }

    def localUsers = for {
      query <- Users.search(Location(location.getOrElse("San Francisco")), Users.SortedByFollowers, Desc)
      queries <- query match {
        case None => ???
        case Some(qs) =>
          Future.sequence(qs.map(_.user))
      }
    } yield queries.sequence.getOrElse(Nil)

    def repositories: Future[List[Repository]] = {
      val url = root / "users" / login / "repos"
      for {
        result <- Http(url <:< globalHeaders OK as.String)
      } yield Parse.parse(result) match {
        case -\/(e) => throw new Exception(e)
        case \/-(json) =>
          json.array.map { o =>
            o.map { x => RepositoryCodecJson.Decoder(x.hcursor).toEither match {
              case Left(e) => throw new Exception(x + "\n" + e._1.toString + e._2.toString)
              case Right(s) => some(s)
            }
          }.sequence
        }.flatten.getOrElse(Nil)
      }
    }

    def languages = for {
      repos <- repositories
      len <- Future.successful { repos.length }
    } yield repos.groupBy { x =>
      x.language.getOrElse("")
    }.map {
      case (k, v) => (k, v.length.toFloat / len)
    }
  }

  implicit def UserCodecJson =
    casecodec19(User.apply, User.unapply)("login", "id", "avatar_url", "gravatar_id",
                                          "url", "name", "company", "blog", "location",
                                          "email", "hireable", "bio", "public_repos",
                                          "public_gists", "followers", "following",
                                          "html_url", "created_at", "type")
}
