package geocodr.github.search

import com.github.nscala_time.time.Imports._

import dispatch._
import Defaults._
import scalaz._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.traverse._
import argonaut._
import Argonaut._
import geocodr.github.user._

object Users {
  case class RepositoryCount(sc: Constraint[Int]) extends UserSearchQuery {
    def query = s"repos:${sc.filter}"
  }

  case class Created(date: Constraint[DateTime]) extends UserSearchQuery {
    def query = s"created:${date.filter}"
  }

  case class Followers(sc: Constraint[Int]) extends UserSearchQuery {
    override def query = s"followers:${sc.filter}"
  }

  /* SearchSort */
  sealed trait UsersSearchSort {
    def sort: String
  }

  case object SortedByFollowers extends UsersSearchSort {
    def sort = "followers"
  }

  case object SortedByRepositories extends UsersSearchSort {
    def sort = "repositories"
  }

  case object SortedByJoined extends UsersSearchSort {
    def sort = "joined"
  }

  type Url = Req

  implicit def UserSearchDecodeJson: DecodeJson[UserSearch] =
    DecodeJson(c => for {
      login <- (c --\ "login").as[String]
      id <- (c --\ "id").as[Long]
      avatar <- (c --\ "avatar_url").as[String]
      gravatarId <- (c --\ "gravatar_id").as[Option[String]]
      followers <- (c --\ "followers_url").as[String]
      subscriptions <- (c --\ "subscriptions_url").as[String]
      organizations <- (c --\ "organizations_url").as[String]
      repos <- (c --\ "repos_url").as[String]
      receivedEvents <- (c --\ "received_events_url").as[String]
      accountType <- (c --\ "type").as[String]
      score <- (c --\ "score").as[Double]
    } yield
      UserSearch(
        login,
        id,
        url(avatar),
        gravatarId,
        url(followers),
        url(subscriptions),
        url(organizations),
        url(repos),
        url(receivedEvents),
        accountType,
        score
      )
    )

  /* terrible line gotta fix */
  implicit def UserSearchEncodeJson: EncodeJson[UserSearch] =
    jencode11L((u: UserSearch) =>
      (u.login, u.id, u.avatar.url, u.gravatarId, u.followers.url, u.subscriptions.url, u.organizations.url, u.repos.url, u.receivedEvents.url, u.accountType, u.score))("login", "id", "avatar", "gravatar_id", "followers_url", "subscriptions_url", "organizations_url", "repos_url", "received_events_url", "type", "score")

  case class UserSearch(
      login: String,
      id: Long,
      avatar: Url,
      gravatarId: Option[String],
      followers: Url,
      subscriptions: Url,
      organizations: Url,
      repos: Url,
      receivedEvents: Url,
      accountType: String,
      score: Double
   ) {

    def user = {
      val url = root / "users" / login
      for {
       rawJson <- Http(url OK as.String)
      } yield rawJson.decodeOption[User]
    }
  }

  def search(sq: CompoundQuery[UserSearchQuery], s: UsersSearchSort, o: Order)/*: Future[UserSearch]*/ = {
    val url = root / "search" / "users"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order, "username" -> System.getenv("GITUSER"), "password" ->
      System.getenv("GITPASS"))
    val future = Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
    for {
      result <- future
    } yield Parse.parse(result) match {
      case -\/(e) => throw new Exception(e)
      case \/-(json) =>
        (json -| "items").map(_.array).flatten.map { is =>
          is.map(x => x.as[UserSearch].toEither match { case Left(e) => throw new Exception(e.toString); case Right(v) => v })
        }
    }
  }
}
