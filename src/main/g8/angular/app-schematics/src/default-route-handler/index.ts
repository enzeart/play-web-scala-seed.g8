import {
  apply,
  Rule,
  SchematicContext,
  SchematicsException,
  template,
  Tree,
  url,
  mergeWith,
} from "@angular-devkit/schematics";
import { createAppRoutingModuleSourceFile, FilePaths } from "../utils/files";
import { findNodes } from "../utility/ast-utils";
import { insertImport } from "../utility/ast-utils";
import { buildRelativePath } from "../utility/find-module";
import * as ts from "typescript";
import { InsertChange } from "../utility/change";

const findRoutesArrayLiteral = (
  sourceFile: ts.SourceFile
): ts.ArrayLiteralExpression => {
  const variableStatementText = "const routes: Routes = [];";
  const routesArrayVariableStatement = findNodes(
    sourceFile,
    ts.isVariableStatement
  ).find((s) => s.getText() === variableStatementText);
  if (!routesArrayVariableStatement)
    throw new SchematicsException("Could not find routes variable statement");
  const routesArrayLiteralExpression = findNodes(
    routesArrayVariableStatement,
    ts.isArrayLiteralExpression
  )[0];
  if (!routesArrayLiteralExpression)
    throw new SchematicsException("Could not find routes array literal");
  return routesArrayLiteralExpression;
};

// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function defaultRouteHandler(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const appRoutingModuleSourceFile = createAppRoutingModuleSourceFile(tree);
    const templateSources = apply(url("./files"), [template({})]);
    const importDefaultRouteHandler = insertImport(
      appRoutingModuleSourceFile,
      FilePaths.APP_ROUTING_MODULE,
      "DefaultRouteHandlerComponent",
      buildRelativePath(
        FilePaths.APP_ROUTING_MODULE,
        "/src/app/core/components/default-route-handler/default-route-handler.component"
      )
    );

    const routesArrayLiteral = findRoutesArrayLiteral(
      appRoutingModuleSourceFile
    );
    const position = routesArrayLiteral.end - 1;
    const route = `{ path: '', component: DefaultRouteHandlerComponent, pathMatch: 'full' }`;
    const changes = [
      new InsertChange(FilePaths.APP_ROUTING_MODULE, position, route),
      importDefaultRouteHandler,
    ];
    const recorder = tree.beginUpdate(FilePaths.APP_ROUTING_MODULE);
    for (const change of changes) {
      if (change instanceof InsertChange) {
        recorder.insertLeft(change.pos, change.toAdd);
      }
    }
    tree.commitUpdate(recorder);

    return mergeWith(templateSources)(tree, _context);
  };
}
