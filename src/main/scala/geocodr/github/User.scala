package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._

object user {

  type Url = Req
  implicit def UserDecodeJSON: DecodeJson[User] =
    DecodeJson(c => for {
      login <- (c --\ "login").as[String]
      id <- (c --\ "id").as[Long]
      avatar <- (c --\ "avatar").as[String]
      followers <- (c --\ "followers").as[String]
      subscriptions <- (c --\ "subscriptions").as[String]
      organizations <- (c --\ "organizations").as[String]
      repos <- (c --\ "repos").as[String]
      receivedEvents <- (c --\ "receivedEvents").as[String]
      accountType <- (c --\ "accountType").as[String]
      score <- (c --\ "score").as[Double] 
    } yield 
      User(
        login,
        id,
        url(avatar),
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
  implicit def UserEncodeJson: EncodeJson[User] =
    jencode10L((u: User) => 
      (u.login, u.id, u.avatar.url, u.followers.url, u.subscriptions.url, u.organizations.url, u.repos.url, u.receivedEvents.url, u.accountType, u.score))("login", "id", "avatar", "followers", "subscriptions", "organizations", "repos", "receivedEvents", "accountType", "score")

  case class User(
    login: String, 
    id: Long, 
    avatar: Url, 
    followers: Url, 
    subscriptions: Url, 
    organizations: Url, 
    repos: Url, 
    receivedEvents: Url,
    accountType: String,
    score: Double
  ) {
    def langaugesUsed = ???
    def repositories = ???
  }
}


