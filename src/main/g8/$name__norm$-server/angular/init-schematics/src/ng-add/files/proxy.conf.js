/*
 * Documentation on the available options can be found here:
 * https://github.com/http-party/node-http-proxy#options
 * */

const PROXY_CONFIG = [
  {
    context: ["/api", "/auth"],
    target: "http://localhost:9000"
  }
];

module.exports = PROXY_CONFIG;

