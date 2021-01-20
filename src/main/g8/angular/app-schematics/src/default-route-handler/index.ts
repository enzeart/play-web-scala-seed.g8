import { mergeWith, Rule, SchematicContext, SchematicsException, Tree } from '@angular-devkit/schematics';
import { applyStandardTemplates, createAppRoutingModuleSourceFile, FilePaths } from '../utils/files';
import { findNodes, insertImport } from '../utility/ast-utils';
import { buildRelativePath } from '../utility/find-module';
import * as ts from 'typescript';
import { InsertChange } from '../utility/change';

const routesVariableStatementText = 'const routes: Routes = [];';

const defaultRouteDefinitionText = `{ path: '', component: DefaultRouteHandlerComponent, pathMatch: 'full' }`;

const defaultRouteHandlerComponentClassifiedName = 'DefaultRouteHandlerComponent';

const relativePathToDefaultRouteHandlerComponent = buildRelativePath(
  FilePaths.APP_ROUTING_MODULE,
  '/src/app/core/components/default-route-handler/default-route-handler.component'
);

const findRoutesArrayLiteral = (sourceFile: ts.SourceFile): ts.ArrayLiteralExpression => {
  const routesArrayVariableStatement = findNodes(sourceFile, ts.isVariableStatement).find(
    (s) => s.getText() === routesVariableStatementText
  );
  if (!routesArrayVariableStatement) throw new SchematicsException('Could not find routes variable statement');
  const routesArrayLiteralExpression = findNodes(routesArrayVariableStatement, ts.isArrayLiteralExpression)[0];
  if (!routesArrayLiteralExpression) throw new SchematicsException('Could not find routes array literal');
  return routesArrayLiteralExpression;
};

export function defaultRouteHandler(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    const appRoutingModuleSourceFile = createAppRoutingModuleSourceFile(tree);
    const importDefaultRouteHandler = insertImport(
      appRoutingModuleSourceFile,
      FilePaths.APP_ROUTING_MODULE,
      defaultRouteHandlerComponentClassifiedName,
      relativePathToDefaultRouteHandlerComponent
    );

    const routesArrayLiteral = findRoutesArrayLiteral(appRoutingModuleSourceFile);
    const position = routesArrayLiteral.end - 1;
    const changes = [
      new InsertChange(FilePaths.APP_ROUTING_MODULE, position, defaultRouteDefinitionText),
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
