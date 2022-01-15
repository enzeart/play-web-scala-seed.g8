package models.auth

case class CommonProfile(
    id: String,
    userName: Option[String],
    email: Option[String],
    firstName: Option[String],
    familyName: Option[String],
    displayName: Option[String],
    roles: Set[String]
) extends UserProfile
