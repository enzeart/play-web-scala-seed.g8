import { mergeWith, Rule, SchematicContext, SchematicsException, Tree } from '@angular-devkit/schematics';
import { applyStandardTemplates, createAppRoutingModuleSourceFile, FilePaths } from '../utils/files';
import { findNodes, insertImport } from '../utility/ast-utils';
import { buildRelativePath } from '../utility/find-module';
import * as ts from 'typescript';
import { InsertChange } from '../utility/change';

const routesVariableStatementText = 'const routes: Routes = [];';

const spaRootComponentClassifiedName = 'SpaRootComponent';

const spaRootRouteDefinitionText = `{ path: '', component: ${spaRootComponentClassifiedName}, pathMatch: 'full' }`;

const relativePathToSpaRootComponent = buildRelativePath(
  FilePaths.APP_ROUTING_MODULE,
  '/src/app/core/components/spa-root/spa-root.component'
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

export function spaRoot(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    const appRoutingModuleSourceFile = createAppRoutingModuleSourceFile(tree);
    const importSpaRootComponent = insertImport(
      appRoutingModuleSourceFile,
      FilePaths.APP_ROUTING_MODULE,
      spaRootComponentClassifiedName,
      relativePathToSpaRootComponent
    );

    const routesArrayLiteral = findRoutesArrayLiteral(appRoutingModuleSourceFile);
    const position = routesArrayLiteral.end - 1;
    const changes = [
      new InsertChange(FilePaths.APP_ROUTING_MODULE, position, spaRootRouteDefinitionText),
      importSpaRootComponent,
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
