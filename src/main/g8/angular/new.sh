#!/bin/bash

set -e

ORIGINAL_DIR="$(pwd)"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

SCHEMATICS_PACKAGE_NAME="init-schematics"
SCHEMATICS_DIR="$SCRIPT_DIR/$SCHEMATICS_PACKAGE_NAME"

ANGULAR_APP_DIR_NAME="ui"
ANGULAR_APP_DIR="$ORIGINAL_DIR/$ANGULAR_APP_DIR_NAME"

ng new $ANGULAR_APP_DIR_NAME --routing=true

cd $SCHEMATICS_DIR
npm install
npm run build

cd $ANGULAR_APP_DIR

npm link $(realpath --relative-to=$ANGULAR_APP_DIR $SCHEMATICS_DIR)

ng g $SCHEMATICS_PACKAGE_NAME:core
ng g $SCHEMATICS_PACKAGE_NAME:proxy-config
ng g $SCHEMATICS_PACKAGE_NAME:app-component
ng g $SCHEMATICS_PACKAGE_NAME:spa-root
ng g $SCHEMATICS_PACKAGE_NAME:app-interceptor
ng g $SCHEMATICS_PACKAGE_NAME:graphql

ng add apollo-angular --endpoint="/api/graphql"
