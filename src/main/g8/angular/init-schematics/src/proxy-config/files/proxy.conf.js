const PROXY_CONFIG = [
  {
    context: ["/api", "/auth"],
    target: "http://localhost:9000"
  }
];

module.exports = PROXY_CONFIG;

