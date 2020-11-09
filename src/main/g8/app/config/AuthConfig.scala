package config;

case class AuthConfig(
  clientName: String,
  callbackUrl: String,
  defaultLoginUrl: String,
  defaultLogoutUrl: String
)
