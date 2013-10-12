package geocodr.github.search

import dispatch._
import Defaults._

object Repositories {
  case class Size(sc: Constraint[Int]) extends RepositoriesSearchQuery {
    def query = ???
  }

  case class Created() extends RepositoriesSearchQuery {
    def query = ???
  }

  case class Pushed() extends RepositoriesSearchQuery {
    def query = ???
  }

  case class User() extends RepositoriesSearchQuery {
    def query = ???
  }

  case class Fork(bool: Boolean) extends RepositoriesSearchQuery {
    def query = s"fork:$bool"
  }

  object Stars extends ComparableOps[Int, Stars] {
    val constructor = Stars(_)
  }

  case class Stars(sc: Constraint[Int]) extends RepositoriesSearchQuery {
    override def query = s"stars:${sc.filter}"
  }

  /* RepositoriesSearchSort */
  sealed trait RepositoriesSearchSort {
    def sort: String
  }

  case object SortedByStars extends RepositoriesSearchSort {
    def sort = "stars"
  }

  case object SortedByForks extends RepositoriesSearchSort {
    def sort = "forks"
  }

  case object SortedByUpdated extends RepositoriesSearchSort {
    def sort = "updated"
  }  

  def search(sq: CompoundQuery[RepositoriesSearchQuery], s: RepositoriesSearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
  }

  def userRepos(repositories: Req) = {
    val result = Http(repositories OK as.String)
  }
}
