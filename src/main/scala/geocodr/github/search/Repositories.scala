package geocodr.github.search

import dispatch._
import Defaults._

object Repositories {
  case class Size(sc: SizeConstraint) extends RepositoriesSearchQuery {
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

  object Stars extends ComparableOps[Stars] {
    val constructor = Stars(_)
  }

  case class Stars(sc: SizeConstraint) extends RepositoriesSearchQuery {
    override def query = s"stars:${sc.filter}"
  }

  /* RepositoriesSearchSort */
  sealed trait RepositoriesSearchSort {
    def sort: String
  }

  case object SortByStars extends RepositoriesSearchSort {
    def sort = "stars"
  }

  case object SortByForks extends RepositoriesSearchSort {
    def sort = "forks"
  }

  case object SortByUpdated extends RepositoriesSearchSort {
    def sort = "updated"
  }  

  def search(sq: CompoundQuery[RepositoriesSearchQuery], s: RepositoriesSearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
  }
}
