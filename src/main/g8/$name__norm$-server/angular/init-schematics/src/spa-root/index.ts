import {
  mergeWith,
  Rule,
  SchematicContext,
  SchematicsException,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  createAppModuleSourceFile,
  createAppRoutingModuleSourceFile,
  createSourceFile,
  FilePaths,
} from '../utils/files';
import {
  addDeclarationToModule,
  findNodes,
  insertImport,
} from '@schematics/angular/utility/ast-utils';
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
  buildRelativePath(
    from,
    '/src/app/core/components/spa-root/spa-root.component'
  );

const findRoutesArrayLiteral = (
  sourceFile: ts.SourceFile
): ts.ArrayLiteralExpression => {
  const routesArrayVariableStatement = findNodes(
    sourceFile,
    ts.isVariableStatement
  ).find((s) => s.getText() === routesVariableStatementText);

  if (!routesArrayVariableStatement)
    throw new SchematicsException('Could not find routes variable statement');

  const routesArrayLiteralExpression = findNodes(
    routesArrayVariableStatement,
    ts.isArrayLiteralExpression
  )[0];

  if (!routesArrayLiteralExpression)
    throw new SchematicsException('Could not find routes array literal');

  return routesArrayLiteralExpression;
};

const findEnvironmentObjectLiteral = (
  sourceFile: ts.SourceFile
): ts.ObjectLiteralExpression => {
  const environmentObjectLiteralExpression = findNodes(
    sourceFile,
    ts.isObjectLiteralExpression
  )[0];

  if (!environmentObjectLiteralExpression)
    throw new SchematicsException('Could not find object literal');

  return environmentObjectLiteralExpression;
};

const editEnvironmentConfiguration = (path: string, tree: Tree): void => {
  const changes = [
    new InsertChange(
      path,
      findEnvironmentObjectLiteral(createSourceFile(path, tree)).end - 2,
      ",\n\tredirectRouteQueryParam: 'spa-redirect-route'"
    ),
  ];

  const recorder = tree.beginUpdate(path);

  for (const change of changes) {
    recorder.insertLeft(change.pos, change.toAdd);
  }

  tree.commitUpdate(recorder);
};

export function spaRoot(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const appRoutingModuleSourceFile = createAppRoutingModuleSourceFile(tree);
    const appModuleSourceFile = createAppModuleSourceFile(tree);
    const importSpaRootComponent = insertImport(
      appRoutingModuleSourceFile,
      FilePaths.APP_ROUTING_MODULE,
      spaRootComponentClassifiedName,
      relativePathToSpaRootComponent(FilePaths.APP_ROUTING_MODULE)
    );
    // const environmentSourceFile = createEnvironmentSourceFile(tree);

    const routesArrayLiteral = findRoutesArrayLiteral(
      appRoutingModuleSourceFile
    );
    const routesArrayPosition = routesArrayLiteral.end - 1;
    const appRoutingModuleChanges = [
      new InsertChange(
        FilePaths.APP_ROUTING_MODULE,
        routesArrayPosition,
        spaRootRouteDefinitionText
      ),
      importSpaRootComponent,
    ];

    const appRoutingModuleRecorder = tree.beginUpdate(
      FilePaths.APP_ROUTING_MODULE
    );

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

    editEnvironmentConfiguration(FilePaths.ENVIRONMENT, tree);
    editEnvironmentConfiguration(FilePaths.ENVIRONMENT_PROD, tree);

    return mergeWith(templateSources)(tree, _context);
  };
}
