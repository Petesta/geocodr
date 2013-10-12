package geocodr.github.search

import dispatch._
import Defaults._

object Repository {
  case class Size(sc: SizeConstraint) extends RepositorySearchQuery
  case class Forks(sc: SizeConstraint) extends RepositorySearchQuery
  //case class Fork()
  case class Created() extends RepositorySearchQuery
  case class Pushed() extends RepositorySearchQuery
  case class User() extends RepositorySearchQuery

  case class Language(name: String) extends RepositorySearchQuery {
    override def query = s"language:${name.map(_.toLower).mkString("")}"
  }

  object Stars {
    def ==(x: Int) = Stars(Eq(x))
    def <(x: Int) = Stars(LessThan(x))
    def <=(x: Int) = Stars(LessThanEq(x))
    def >(x: Int)  = Stars(GreaterThan(x))
    def >=(x: Int) = Stars(GreaterThanEq(x))
    def range(pair: (Int, Int)) = Stars(Range(pair._1, pair._2))
  }

  case class Stars(sc: SizeConstraint) extends RepositorySearchQuery {
    override def query = s"stars:${sc.filter}"
  }

  /* SearchSort */
  sealed trait SearchSort {
    def sort: String
  }

  case object SortByStars extends SearchSort {
    def sort = "stars"
  }

  case object SortByForks extends SearchSort {
    def sort = "forks"
  }

  case object SortByUpdated extends SearchSort {
    def sort = "updated"
  }  

  def search(sq: RepositorySearchQuery, s: SearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
  }
}
