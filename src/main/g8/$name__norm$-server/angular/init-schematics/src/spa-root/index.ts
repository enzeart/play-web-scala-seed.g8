import {
  mergeWith,
  Rule,
  SchematicContext,
  SchematicsException,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  // createAppRoutingModuleSourceFile,
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

// const routesVariableStatementText = 'const routes: Routes = [];';

const spaRootComponentClassifiedName = 'SpaRootComponent';

// const spaRootRouteDefinitionText = `
//   { path: '', component: ${spaRootComponentClassifiedName}, pathMatch: 'full' },
//   { path: '**', redirectTo: '/' }
// `;

const spaRootComponentRelativePath = (from: string) =>
  buildRelativePath(
    from,
    '/src/app/core/components/spa-root/spa-root.component'
  );

// const findRoutesArrayLiteral = (
//   sourceFile: ts.SourceFile
// ): ts.ArrayLiteralExpression => {
//   const routesArrayVariableStatement = findNodes(
//     sourceFile,
//     ts.isVariableStatement
//   ).find((s) => s.getText() === routesVariableStatementText);
//
//   if (!routesArrayVariableStatement)
//     throw new SchematicsException('Could not find routes variable statement');
//
//   const routesArrayLiteralExpression = findNodes(
//     routesArrayVariableStatement,
//     ts.isArrayLiteralExpression
//   )[0];
//
//   if (!routesArrayLiteralExpression)
//     throw new SchematicsException('Could not find routes array literal');
//
//   return routesArrayLiteralExpression;
// };

const editAppRoutingModule = (tree: Tree): void => {
  const sourceFile = createSourceFile(FilePaths.APP_ROUTING_MODULE, tree);
  const routesVariableStatement = findNodes(
    sourceFile,
    ts.isVariableStatement
  ).find((statement) => statement.getText() === 'const routes: Routes = [];');

  if (!routesVariableStatement)
    throw new SchematicsException('Could not find "routes" variable statement');

  const routesExpression = findNodes(
    routesVariableStatement,
    ts.isArrayLiteralExpression
  )[0];

  if (!routesExpression)
    throw new SchematicsException('Could not find "routes" array literal');

  const changes = [
    insertImport(
      sourceFile,
      FilePaths.APP_ROUTING_MODULE,
      spaRootComponentClassifiedName,
      spaRootComponentRelativePath(FilePaths.APP_ROUTING_MODULE)
    ),
    new InsertChange(
      FilePaths.APP_ROUTING_MODULE,
      routesExpression.end - 1,
      `
        { path: '', component: ${spaRootComponentClassifiedName}, pathMatch: 'full' },
        { path: '**', redirectTo: '/' }
      `
    ),
  ];

  const recorder = tree.beginUpdate(FilePaths.APP_ROUTING_MODULE);

  for (const change of changes) {
    if (change instanceof InsertChange) {
      recorder.insertLeft(change.pos, change.toAdd);
    }
  }

  tree.commitUpdate(recorder);
};

const editAppModule = (tree: Tree): void => {
  const changes = addDeclarationToModule(
    createSourceFile(FilePaths.APP_MODULE, tree),
    FilePaths.APP_MODULE,
    spaRootComponentClassifiedName,
    spaRootComponentRelativePath(FilePaths.APP_MODULE)
  );

  const recorder = tree.beginUpdate(FilePaths.APP_MODULE);

  for (const change of changes) {
    if (change instanceof InsertChange) {
      recorder.insertLeft(change.pos, change.toAdd);
    }
  }

  tree.commitUpdate(recorder);
};

const editEnvironmentConfiguration = (path: string, tree: Tree): void => {
  const expression = findNodes(
    createSourceFile(path, tree),
    ts.isObjectLiteralExpression
  )[0];

  if (!expression)
    throw new SchematicsException(
      'Could not find environment object literal expression'
    );

  const changes = [
    new InsertChange(
      path,
      expression.end - 2,
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
    // const appRoutingModuleSourceFile = createAppRoutingModuleSourceFile(tree);
    // const importSpaRootComponent = insertImport(
    //   appRoutingModuleSourceFile,
    //   FilePaths.APP_ROUTING_MODULE,
    //   spaRootComponentClassifiedName,
    //   spaRootComponentRelativePath(FilePaths.APP_ROUTING_MODULE)
    // );
    //
    // const routesArrayLiteral = findRoutesArrayLiteral(
    //   appRoutingModuleSourceFile
    // );
    // const routesArrayPosition = routesArrayLiteral.end - 1;
    // const appRoutingModuleChanges = [
    //   new InsertChange(
    //     FilePaths.APP_ROUTING_MODULE,
    //     routesArrayPosition,
    //     spaRootRouteDefinitionText
    //   ),
    //   importSpaRootComponent,
    // ];
    //
    // const appRoutingModuleRecorder = tree.beginUpdate(
    //   FilePaths.APP_ROUTING_MODULE
    // );
    //
    // for (const change of appRoutingModuleChanges) {
    //   if (change instanceof InsertChange) {
    //     appRoutingModuleRecorder.insertLeft(change.pos, change.toAdd);
    //   }
    // }
    //
    // tree.commitUpdate(appRoutingModuleRecorder);

    editAppRoutingModule(tree);
    editAppModule(tree);
    editEnvironmentConfiguration(FilePaths.ENVIRONMENT, tree);
    editEnvironmentConfiguration(FilePaths.ENVIRONMENT_PROD, tree);

    return mergeWith(templateSources)(tree, _context);
  };
}
