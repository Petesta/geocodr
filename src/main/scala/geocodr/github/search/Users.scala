package geocodr.github.search

import dispatch._
import Defaults._

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

  case object SortByFollowers extends UsersSearchSort {
    def sort = "followers"
  }

  case object SortByRepositories extends UsersSearchSort {
    def sort = "repositories"
  }

  case object SortByJoined extends UsersSearchSort {
    def sort = "joined"
  }  

  def search(sq: CompoundQuery[UserSearchQuery], s: UsersSearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
  }
}
