package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._
import geocodr.github.search.Users.{ UserSearch, UserSearchEncodeJson, UserSearchDecodeJson}

object repository {
  /*
  {
    "id": 330651,
    "name": "bert",
    "full_name": "mojombo/bert",
    "owner": {
      "login": "mojombo",
      "id": 1,
      "avatar_url": "https://1.gravatar.com/avatar/25c7c18223fb42a4c6ae1c8db6f50f9b?d=https%3A%2F%2Fidenticons.github.com%2Fc4ca4238a0b923820dcc509a6f75849b.png",
      "gravatar_id": "25c7c18223fb42a4c6ae1c8db6f50f9b",
      "url": "https://api.github.com/users/mojombo",
      "html_url": "https://github.com/mojombo",
      "followers_url": "https://api.github.com/users/mojombo/followers",
      "following_url": "https://api.github.com/users/mojombo/following{/other_user}",
      "gists_url": "https://api.github.com/users/mojombo/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
      "organizations_url": "https://api.github.com/users/mojombo/orgs",
      "repos_url": "https://api.github.com/users/mojombo/repos",
      "events_url": "https://api.github.com/users/mojombo/events{/privacy}",
      "received_events_url": "https://api.github.com/users/mojombo/received_events",
      "type": "User",
      "site_admin": true
    },
    "private": false,
    "html_url": "https://github.com/mojombo/bert",
    "description": "BERT (Binary ERlang Term) serialization library for Ruby.",
    "fork": false,
    "url": "https://api.github.com/repos/mojombo/bert",
    "forks_url": "https://api.github.com/repos/mojombo/bert/forks",
    "keys_url": "https://api.github.com/repos/mojombo/bert/keys{/key_id}",
    "collaborators_url": "https://api.github.com/repos/mojombo/bert/collaborators{/collaborator}",
    "teams_url": "https://api.github.com/repos/mojombo/bert/teams",
    "hooks_url": "https://api.github.com/repos/mojombo/bert/hooks",
    "issue_events_url": "https://api.github.com/repos/mojombo/bert/issues/events{/number}",
    "events_url": "https://api.github.com/repos/mojombo/bert/events",
    "assignees_url": "https://api.github.com/repos/mojombo/bert/assignees{/user}",
    "branches_url": "https://api.github.com/repos/mojombo/bert/branches{/branch}",
    "tags_url": "https://api.github.com/repos/mojombo/bert/tags",
    "blobs_url": "https://api.github.com/repos/mojombo/bert/git/blobs{/sha}",
    "git_tags_url": "https://api.github.com/repos/mojombo/bert/git/tags{/sha}",
    "git_refs_url": "https://api.github.com/repos/mojombo/bert/git/refs{/sha}",
    "trees_url": "https://api.github.com/repos/mojombo/bert/git/trees{/sha}",
    "statuses_url": "https://api.github.com/repos/mojombo/bert/statuses/{sha}",
    "languages_url": "https://api.github.com/repos/mojombo/bert/languages",
    "stargazers_url": "https://api.github.com/repos/mojombo/bert/stargazers",
    "contributors_url": "https://api.github.com/repos/mojombo/bert/contributors",
    "subscribers_url": "https://api.github.com/repos/mojombo/bert/subscribers",
    "subscription_url": "https://api.github.com/repos/mojombo/bert/subscription",
    "commits_url": "https://api.github.com/repos/mojombo/bert/commits{/sha}",
    "git_commits_url": "https://api.github.com/repos/mojombo/bert/git/commits{/sha}",
    "comments_url": "https://api.github.com/repos/mojombo/bert/comments{/number}",
    "issue_comment_url": "https://api.github.com/repos/mojombo/bert/issues/comments/{number}",
    "contents_url": "https://api.github.com/repos/mojombo/bert/contents/{+path}",
    "compare_url": "https://api.github.com/repos/mojombo/bert/compare/{base}...{head}",
    "merges_url": "https://api.github.com/repos/mojombo/bert/merges",
    "archive_url": "https://api.github.com/repos/mojombo/bert/{archive_format}{/ref}",
    "downloads_url": "https://api.github.com/repos/mojombo/bert/downloads",
    "issues_url": "https://api.github.com/repos/mojombo/bert/issues{/number}",
    "pulls_url": "https://api.github.com/repos/mojombo/bert/pulls{/number}",
    "milestones_url": "https://api.github.com/repos/mojombo/bert/milestones{/number}",
    "notifications_url": "https://api.github.com/repos/mojombo/bert/notifications{?since,all,participating}",
    "labels_url": "https://api.github.com/repos/mojombo/bert/labels{/name}",
    "created_at": "2009-10-08T06:06:25Z",
    "updated_at": "2013-09-20T07:22:41Z",
    "pushed_at": "2012-05-25T22:03:32Z",
    "git_url": "git://github.com/mojombo/bert.git",
    "ssh_url": "git@github.com:mojombo/bert.git",
    "clone_url": "https://github.com/mojombo/bert.git",
    "svn_url": "https://github.com/mojombo/bert",
    "homepage": "",
    "size": 184,
    "watchers_count": 138,
    "language": "Ruby",
    "has_issues": true,
    "has_downloads": true,
    "has_wiki": true,
    "forks_count": 34,
    "mirror_url": null,
    "open_issues_count": 7,
    "forks": 34,
    "open_issues": 7,
    "watchers": 138,
    "master_branch": "master",
    "default_branch": "master"
   */

  /* Without Comments
    case class Repository(
    id: Long,               // "id": 330651,
    owner: UserSearch,
    name: String,           // "name": "bert",
    fullName: String,       // "full_name": "mojombo/bert"
    description: String,
    priv: Boolean,          // "private": false,
    htmlUrl: String, // "html_url": "https://github.com/mojombo/bert",
    description: String,  // "description": "BERT (Binary ERlang Term) serialization library for Ruby."
    fork: Boolean,  // "fork": false,
    url: String,    // "url": "https://api.github.com/repos/mojombo/bert",
    homepage: String,
    watchersCount: Long, //"watchers_count": 138,
    language: String,   //"language": "Ruby",
    masterBranch: String, //"master_branch": "master",
    defaultBranch: String //"default_branch": "master"
  */

  case class Repository(
    id: Long,               // "id": 330651,
    /* "owner": {
      "login": "mojombo",
      "id": 1,
      "avatar_url": "https://1.gravatar.com/avatar/25c7c18223fb42a4c6ae1c8db6f50f9b?d=https%3A%2F%2Fidenticons.github.com%2Fc4ca4238a0b923820dcc509a6f75849b.png",
      "gravatar_id": "25c7c18223fb42a4c6ae1c8db6f50f9b",
      "url": "https://api.github.com/users/mojombo",
      "html_url": "https://github.com/mojombo",
      "followers_url": "https://api.github.com/users/mojombo/followers",
      "following_url": "https://api.github.com/users/mojombo/following{/other_user}",
      "gists_url": "https://api.github.com/users/mojombo/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
      "organizations_url": "https://api.github.com/users/mojombo/orgs",
      "repos_url": "https://api.github.com/users/mojombo/repos",
      "events_url": "https://api.github.com/users/mojombo/events{/privacy}",
      "received_events_url": "https://api.github.com/users/mojombo/received_events",
      "type": "User",
      "site_admin": true
    } */
    owner: UserSearch,
    name: String,           // "name": "bert",
    fullName: String,       // "full_name": "mojombo/bert"
    description: String,
    priv: Boolean,          // "private": false,
    htmlUrl: String,// "html_url": "https://github.com/mojombo/bert",
    description: String, // "description": "BERT (Binary ERlang Term) serialization library for Ruby."
    fork: Boolean,  // "fork": false,
    url: String,    // "url": "https://api.github.com/repos/mojombo/bert",
    /* "forks_url": "https://api.github.com/repos/mojombo/bert/forks",
       "keys_url": "https://api.github.com/repos/mojombo/bert/keys{/key_id}",
       "collaborators_url": "https://api.github.com/repos/mojombo/bert/collaborators{/collaborator}",
       "teams_url": "https://api.github.com/repos/mojombo/bert/teams",
       "hooks_url": "https://api.github.com/repos/mojombo/bert/hooks",
       "issue_events_url": "https://api.github.com/repos/mojombo/bert/issues/events{/number}",
       "events_url": "https://api.github.com/repos/mojombo/bert/events",
       "assignees_url": "https://api.github.com/repos/mojombo/bert/assignees{/user}",
       "branches_url": "https://api.github.com/repos/mojombo/bert/branches{/branch}",
       "tags_url": "https://api.github.com/repos/mojombo/bert/tags",
       "blobs_url": "https://api.github.com/repos/mojombo/bert/git/blobs{/sha}",
       "git_tags_url": "https://api.github.com/repos/mojombo/bert/git/tags{/sha}",
       "git_refs_url": "https://api.github.com/repos/mojombo/bert/git/refs{/sha}",
       "trees_url": "https://api.github.com/repos/mojombo/bert/git/trees{/sha}",
       "statuses_url": "https://api.github.com/repos/mojombo/bert/statuses/{sha}",
       "languages_url": "https://api.github.com/repos/mojombo/bert/languages",
       "stargazers_url": "https://api.github.com/repos/mojombo/bert/stargazers",
       "contributors_url": "https://api.github.com/repos/mojombo/bert/contributors",
       "subscribers_url": "https://api.github.com/repos/mojombo/bert/subscribers",
       "subscription_url": "https://api.github.com/repos/mojombo/bert/subscription",
       "commits_url": "https://api.github.com/repos/mojombo/bert/commits{/sha}",
       "git_commits_url": "https://api.github.com/repos/mojombo/bert/git/commits{/sha}",
       "comments_url": "https://api.github.com/repos/mojombo/bert/comments{/number}",
       "issue_comment_url": "https://api.github.com/repos/mojombo/bert/issues/comments/{number}",
       "contents_url": "https://api.github.com/repos/mojombo/bert/contents/{+path}",
       "compare_url": "https://api.github.com/repos/mojombo/bert/compare/{base}...{head}",
       "merges_url": "https://api.github.com/repos/mojombo/bert/merges",
       "archive_url": "https://api.github.com/repos/mojombo/bert/{archive_format}{/ref}",
       "downloads_url": "https://api.github.com/repos/mojombo/bert/downloads",
       "issues_url": "https://api.github.com/repos/mojombo/bert/issues{/number}",
       "pulls_url": "https://api.github.com/repos/mojombo/bert/pulls{/number}",
       "milestones_url": "https://api.github.com/repos/mojombo/bert/milestones{/number}",
       "notifications_url": "https://api.github.com/repos/mojombo/bert/notifications{?since,all,participating}",
       "labels_url": "https://api.github.com/repos/mojombo/bert/labels{/name}",
       "created_at": "2009-10-08T06:06:25Z",
       "updated_at": "2013-09-20T07:22:41Z",
       "pushed_at": "2012-05-25T22:03:32Z",
       "git_url": "git://github.com/mojombo/bert.git",
       "ssh_url": "git@github.com:mojombo/bert.git",
       "clone_url": "https://github.com/mojombo/bert.git",
       "svn_url": "https://github.com/mojombo/bert", */
    homepage: String,
    // "size": 184,
    watchersCount: Long, //"watchers_count": 138,
    language: String,   //"language": "Ruby",
    //"has_issues": true,
    //"has_downloads": true,
    //"has_wiki": true,
    //"forks_count": 34,
    //"mirror_url": null,
    //"open_issues_count": 7,
    //"forks": 34,
    //"open_issues": 7,
    //"watchers": 138,
    masterBranch: String, //"master_branch": "master",
    defaultBranch: String //"default_branch": "master" */
  )
}

