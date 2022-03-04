package config

case class AuthConfig(
    clientName: String,
    callbackUrl: String,
    defaultLoginUrl: String,
    defaultLogoutUrl: String,
    $if(oidc_enabled)$
    clientId: String,
    secret: String,
    discoveryUri: String,
    $endif$
)
