package models.auth

trait UserProfile {

  val id: String
  val userName: Option[String]
  val email: Option[String]
  val firstName: Option[String]
  val familyName: Option[String]
  val displayName: Option[String]
  val roles: Set[String]
}
