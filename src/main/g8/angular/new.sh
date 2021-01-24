#!/bin/bash

set -e

ORIGINAL_DIR="$(pwd)"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

APP_SCHEMATICS_DIR="$SCRIPT_DIR/app-schematics"
APP_SCHEMATICS_PACKAGE_TARBALL="$APP_SCHEMATICS_DIR/app-schematics-0.0.0.tgz"

ANGULAR_APP_DIR_NAME="ui"
ANGULAR_APP_DIR="$ORIGINAL_DIR/$ANGULAR_APP_DIR_NAME"

cd $APP_SCHEMATICS_DIR

npm install
npm run build
npm pack

cd $ORIGINAL_DIR

ng new $ANGULAR_APP_DIR_NAME

cd $ANGULAR_APP_DIR

npm install --save-dev $(realpath --relative-to=$ANGULAR_APP_DIR $APP_SCHEMATICS_PACKAGE_TARBALL)

ng g app-schematics:proxy-config
ng g app-schematics:app-component
ng g app-schematics:spa-root
ng g app-schematics:app-interceptor


