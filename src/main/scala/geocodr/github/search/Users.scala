package geocodr.github.search

import dispatch._
import Defaults._
import scalaz._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.traverse._
import argonaut._
import Argonaut._

object Users {
  case class RepositoryCount(sc: SizeConstraint) extends UserSearchQuery {
    def query = s"repos:${sc.filter}"
  }

  case class Created() extends UserSearchQuery {
    def query = ???
  }

  case class Followers(sc: SizeConstraint) extends UserSearchQuery {
    override def query = s"stars:${sc.filter}"
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

  implicit def UserSearchDecodeJSON: DecodeJson[UserSearch] =
    DecodeJson(c => for {
      login <- (c --\ "login").as[String]
      id <- (c --\ "id").as[Long]
      avatar <- (c --\ "avatar_url").as[String]
      gravatarId <- (c --\ "gravatar_id").as[String]
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
      gravatarId: String,
      followers: Url,
      subscriptions: Url,
      organizations: Url,
      repos: Url,
      receivedEvents: Url,
      accountType: String,
      score: Double
   ) {

    def langaugesUsed(login: String) = {
      val query = search(QueryText(login), SortedByFollowers, Asc)
      for {
        userSearch <- query
        repoSearch <- Repositories.userRepos(userSearch.repos)
      } yield repoSearch
    }

    def repositories = ???
  }
  def search(sq: CompoundQuery[UserSearchQuery], s: UsersSearchSort, o: Order)/*: Future[UserSearch]*/ = {
    val url = root / "search" / "users"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    val future = Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
    for {
      result <- future
    } yield Parse.parse(result) match {
      case -\/(e) => ???
      case \/-(json) =>
        (json -| "items").map(_.array).flatten.map { is =>
          is.map(x => x.as[UserSearch].toOption).sequence
        } match {
          case None => ???
          case Some(v) => v
        }
    }
  }
}
