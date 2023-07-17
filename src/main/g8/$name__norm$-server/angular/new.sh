#!/bin/bash

set -ex

ORIGINAL_DIR="$(pwd)"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

SCHEMATICS_PACKAGE_NAME="init-schematics"
SCHEMATICS_DIR="$SCRIPT_DIR/$SCHEMATICS_PACKAGE_NAME"

ANGULAR_APP_DIR_NAME="${1:-ui}"
ANGULAR_APP_DIR="$ORIGINAL_DIR/$ANGULAR_APP_DIR_NAME"

ng new $ANGULAR_APP_DIR_NAME --routing=true --style=scss --skip-git

cd $SCHEMATICS_DIR
npm install
npm run build

cd $ANGULAR_APP_DIR

npm link $(realpath --relative-to=$ANGULAR_APP_DIR $SCHEMATICS_DIR)

ng add $SCHEMATICS_PACKAGE_NAME --project=$ANGULAR_APP_DIR_NAME

#ng g environments
#ng g $SCHEMATICS_PACKAGE_NAME:core --project=$ANGULAR_APP_DIR_NAME
#ng g $SCHEMATICS_PACKAGE_NAME:proxy-config --project=$ANGULAR_APP_DIR_NAME
#ng g $SCHEMATICS_PACKAGE_NAME:app-component
#ng g $SCHEMATICS_PACKAGE_NAME:spa-root
#ng g $SCHEMATICS_PACKAGE_NAME:app-interceptor
#ng g $SCHEMATICS_PACKAGE_NAME:graphql
#ng g $SCHEMATICS_PACKAGE_NAME:shared-module

ng add --skip-confirmation apollo-angular --endpoint="/api/graphql"

npm install --save ngx-cookie-service

npm install --save-dev @graphql-codegen/cli \
    @graphql-codegen/typescript \
    @graphql-codegen/typescript-apollo-angular \
    @graphql-codegen/typescript-operations \
    @graphql-codegen/introspection \
    @graphql-codegen/time

npx sb init

npm install --save-dev prettier

npm run prettier
