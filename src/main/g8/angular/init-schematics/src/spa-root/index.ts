import { mergeWith, Rule, SchematicContext, SchematicsException, Tree } from '@angular-devkit/schematics';
import {
  applyStandardTemplates,
  createAppModuleSourceFile,
  createAppRoutingModuleSourceFile,
  FilePaths,
} from '../utils/files';
import { addDeclarationToModule, findNodes, insertImport } from '@schematics/angular/utility/ast-utils';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import * as ts from '@schematics/angular/third_party/github.com/Microsoft/TypeScript/lib/typescript';
import { InsertChange } from '@schematics/angular/utility/change';

const routesVariableStatementText = 'const routes: Routes = [];';

const spaRootComponentClassifiedName = 'SpaRootComponent';

const spaRootRouteDefinitionText = `
  { path: '', component: ${spaRootComponentClassifiedName}, pathMatch: 'full' },
  { path: '**', redirectTo: '/' }
`;

const relativePathToSpaRootComponent = (from: string) =>
  buildRelativePath(from, '/src/app/core/components/spa-root/spa-root.component');

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
    const appModuleSourceFile = createAppModuleSourceFile(tree);
    const importSpaRootComponent = insertImport(
      appRoutingModuleSourceFile,
      FilePaths.APP_ROUTING_MODULE,
      spaRootComponentClassifiedName,
      relativePathToSpaRootComponent(FilePaths.APP_ROUTING_MODULE)
    );

    const routesArrayLiteral = findRoutesArrayLiteral(appRoutingModuleSourceFile);
    const position = routesArrayLiteral.end - 1;
    const appRoutingModuleChanges = [
      new InsertChange(FilePaths.APP_ROUTING_MODULE, position, spaRootRouteDefinitionText),
      importSpaRootComponent,
    ];

    const appRoutingModuleRecorder = tree.beginUpdate(FilePaths.APP_ROUTING_MODULE);
    for (const change of appRoutingModuleChanges) {
      if (change instanceof InsertChange) {
        appRoutingModuleRecorder.insertLeft(change.pos, change.toAdd);
      }
    }
    tree.commitUpdate(appRoutingModuleRecorder);

    const addSpaRootComponentDeclaration = addDeclarationToModule(
      appModuleSourceFile,
      FilePaths.APP_MODULE,
      spaRootComponentClassifiedName,
      relativePathToSpaRootComponent(FilePaths.APP_MODULE)
    );

    const appModuleRecorder = tree.beginUpdate(FilePaths.APP_MODULE);
    for (const change of addSpaRootComponentDeclaration) {
      if (change instanceof InsertChange) {
        appModuleRecorder.insertLeft(change.pos, change.toAdd);
      }
    }
    tree.commitUpdate(appModuleRecorder);

    return mergeWith(templateSources)(tree, _context);
  };
}
