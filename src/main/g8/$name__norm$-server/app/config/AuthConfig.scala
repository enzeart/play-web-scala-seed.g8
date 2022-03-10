package config

case class AuthConfig(
    clientName: String,
    callbackUrl: String,
    defaultLoginUrl: String,
    defaultLogoutUrl: String,
    $if(oidc_enabled.truthy)$
    clientId: String,
    secret: String,
    discoveryUri: String,
    $endif$
)
