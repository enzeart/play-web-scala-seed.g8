package graphql
import models.auth.{CommonProfile, UserProfile}
import sangria.schema._

object AuthSchema {

  val UserProfile: InterfaceType[Unit, UserProfile] = InterfaceType(
    "UserProfile",
    "The profile of an authenticated user",
    fields[Unit, UserProfile](
      Field("id", StringType, Some("The user's id"), resolve = _.value.id),
      Field("userName", OptionType(StringType), Some("The user's account name"), resolve = _.value.userName),
      Field("email", OptionType(StringType), Some("The user's email address"), resolve = _.value.email),
      Field("firstName", OptionType(StringType), Some("This user's first name"), resolve = _.value.firstName),
      Field("familyName", OptionType(StringType), Some("The user's family name"), resolve = _.value.familyName),
      Field(
        "displayName",
        OptionType(StringType),
        Some("The the name displayed for the user"),
        resolve = _.value.displayName
      ),
      Field("roles", ListType(StringType), Some("The user's account roles"), resolve = _.value.roles.toSeq)
    )
  )

  val CommonProfile: ObjectType[Unit, CommonProfile] = ObjectType(
    "CommonProfile",
    "A profile with all of the common fields shared across all profile types",
    interfaces[Unit, CommonProfile](UserProfile),
    fields[Unit, CommonProfile](
      Field("id", StringType, Some("The user's id"), resolve = _.value.id),
      Field("userName", OptionType(StringType), Some("The user's account name"), resolve = _.value.userName),
      Field("email", OptionType(StringType), Some("The user's email address"), resolve = _.value.email),
      Field("firstName", OptionType(StringType), Some("This user's first name"), resolve = _.value.firstName),
      Field("familyName", OptionType(StringType), Some("The user's family name"), resolve = _.value.familyName),
      Field(
        "displayName",
        OptionType(StringType),
        Some("The the name displayed for the user"),
        resolve = _.value.displayName
      ),
      Field("roles", ListType(StringType), Some("The user's account roles"), resolve = _.value.roles.toSeq)
    )
  )

  val UserProfilesField: Field[GraphQLContext, Unit] =
    Field("userProfiles", ListType(UserProfile), resolve = _.ctx.userProfiles)

  val Types: List[Type with Named] = UserProfile :: CommonProfile :: Nil
}
