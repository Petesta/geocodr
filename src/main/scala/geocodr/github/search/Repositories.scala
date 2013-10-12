package geocodr.github.search

import dispatch._
import Defaults._
import scalaz._
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.traverse._
import argonaut._
import Argonaut._
import geocodr.github.repository._

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

  implicit def UserRepositoryDecodeJson: DecodeJson[RepositorySearch] =
    DecodeJson(c => for {
      language <- (c --\ "language").as[String]
    } yield
      RepositorySearch(
        language
      )
    )

  implicit def RepositorySearchEncodeJson: EncodeJson[RepositorySearch] =
    jencode1L((r: RepositorySearch) => (r.language))("language")

  case class RepositorySearch(
    language: String
  ) {

    def repository = {
      val url = root / "user" / "repos"
      for {
       rawJson <- Http(url OK as.String)
      } yield rawJson.decodeOption[Repository]
    }
  }


  def search(sq: CompoundQuery[RepositoriesSearchQuery], s: RepositoriesSearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< globalHeaders <<? params OK as.String)
  }

  def listLanguages(sq: CompoundQuery[RepositoriesSearchQuery], s: RepositoriesSearchSort, o: Order) = {
    val future = search(sq, s, o)
    for {
      result <- future
    } yield Parse.parse(result) match {
      case -\/(e) => throw new Exception(e)
      case \/-(json) => 
        (json -| "items").map(_.array).flatten.map { is =>
          is.map(x => x.as[RepositorySearch].toEither match { case Left(e) => throw new Exception(e.toString); case Right(v) => v })
        }
    }
  }

  def userRepos(repositories: Req) = {
    // val result = Http(repositories OK as.String)
  }
}
