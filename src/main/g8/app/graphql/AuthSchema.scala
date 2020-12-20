package graphql

import models.auth.{CommonProfile, UserProfile}
import sangria.macros.derive._
import sangria.schema.{Field, InterfaceType, ListType, Named, ObjectType, OptionType, StringType, Type, fields}

object AuthSchema {

  private val IdFieldDescription          = "The user's id"
  private val UserNameFieldDescription    = "The user's account name"
  private val EmailFieldDescription       = "The user's email address"
  private val FirstNameFieldDescription   = "The user's first name"
  private val FamilyNameFieldDescription  = "The user's family name"
  private val DisplayNameFieldDescription = "The user's publicly displayed name"
  private val RolesFieldDescription       = "The user's account roles"

  implicit val UserProfile: InterfaceType[Unit, UserProfile] = InterfaceType(
    "UserProfile",
    "The profile of an authenticated user",
    fields[Unit, UserProfile](
      Field("id", StringType, Option(IdFieldDescription), resolve = _.value.id),
      Field("userName", OptionType(StringType), Option(UserNameFieldDescription), resolve = _.value.userName),
      Field("email", OptionType(StringType), Option(EmailFieldDescription), resolve = _.value.email),
      Field("firstName", OptionType(StringType), Option(FirstNameFieldDescription), resolve = _.value.firstName),
      Field("familyName", OptionType(StringType), Option(FamilyNameFieldDescription), resolve = _.value.familyName),
      Field("displayName", OptionType(StringType), Option(DisplayNameFieldDescription), resolve = _.value.displayName),
      Field("roles", ListType(StringType), Some(RolesFieldDescription), resolve = _.value.roles.toSeq)
    )
  )

  val CommonProfile: ObjectType[Unit, CommonProfile] = deriveObjectType[Unit, CommonProfile](
    ObjectTypeName("CommonProfile"),
    ObjectTypeDescription("A profile with all of the common fields shared across all profile types"),
    Interfaces(UserProfile),
    ReplaceField(
      "roles",
      Field("roles", ListType(StringType), description = Option(RolesFieldDescription), resolve = _.value.roles.toSeq)
    ),
    DocumentField(fieldName = "id", description = IdFieldDescription),
    DocumentField(fieldName = "userName", description = UserNameFieldDescription),
    DocumentField(fieldName = "email", description = EmailFieldDescription),
    DocumentField(fieldName = "firstName", description = FirstNameFieldDescription),
    DocumentField(fieldName = "familyName", description = FamilyNameFieldDescription),
    DocumentField(fieldName = "displayName", description = DisplayNameFieldDescription)
  )

  val Types: List[Type with Named] = UserProfile :: CommonProfile :: Nil
}
