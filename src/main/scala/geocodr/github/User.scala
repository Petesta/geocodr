package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._

object user {
  implicit def EncodeUserListJson: EncodeJson[List[User]] =
    EncodeJson((us: List[User]) => jArray(us.map(u => UserInfoEncodeJson.encode(u.info))))

  implicit def UserInfoEncodeJson =
    jencode4L((ui: UserInfo) => (ui.name, ui.avatarUrl, ui.location, ui.nearbyUsers))("name", "avatarUrl", "location", "nearbyUsers")

  case class UserInfo(name: String, avatarUrl: String, location: String, nearbyUsers: List[User])

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
    def info = UserInfo(login, avatarUrl, location, Nil)
  }

  implicit def UserCodecJson =
    casecodec19(User.apply, User.unapply)("login", "id", "avatar_url", "gravatar_id",
                                          "url", "name", "company", "blog", "location",
                                          "email", "hireable", "bio", "public_repos",
                                          "public_gists", "followers", "following",
                                          "html_url", "created_at", "type")
}
