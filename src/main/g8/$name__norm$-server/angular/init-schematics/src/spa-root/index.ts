import {
  mergeWith,
  Rule,
  SchematicContext,
  SchematicsException,
  Tree,
} from '@angular-devkit/schematics';
import { applyTemplates, createSourceFile } from '../util/files-utils';
import {
  addDeclarationToModule,
  findNodes,
  insertImport,
} from '@schematics/angular/utility/ast-utils';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import * as ts from '@schematics/angular/third_party/github.com/Microsoft/TypeScript/lib/typescript';
import { InsertChange } from '@schematics/angular/utility/change';
import * as FilePaths from '../util/file-paths';
import * as ImportPaths from '../util/import-paths';
import * as ClassifiedNames from '../util/classified-names';

export function spaRoot(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editAppRoutingModule(tree);
    editAppModule(tree);
    editEnvironmentConfiguration(FilePaths.environmentConfiguration, tree);
    editEnvironmentConfiguration(FilePaths.prodEnvironmentConfiguration, tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const spaRootComponentRelativePath = (from: string) =>
  buildRelativePath(from, ImportPaths.spaRootComponent);

const editAppRoutingModule = (tree: Tree): void => {
  const sourceFile = createSourceFile(FilePaths.appRoutingModule, tree);
  const routesVariableStatement = findNodes(
    sourceFile,
    ts.isVariableStatement
  ).find((statement) =>
    statement.declarationList.declarations.some(
      (declaration) =>
        ts.isIdentifier(declaration.name) &&
        declaration.name.escapedText === 'routes'
    )
  );

  if (!routesVariableStatement)
    throw new SchematicsException('Failed to find "routes" variable statement');

  const routesExpression = findNodes(
    routesVariableStatement,
    ts.isArrayLiteralExpression
  )[0];

  if (!routesExpression)
    throw new SchematicsException('Failed to find "routes" array literal');

  const changes = [
    insertImport(
      sourceFile,
      FilePaths.appRoutingModule,
      ClassifiedNames.spaRootComponent,
      spaRootComponentRelativePath(FilePaths.appRoutingModule)
    ),
    new InsertChange(
      FilePaths.appRoutingModule,
      routesExpression.end - 1,
      `
        { path: '', component: ${ClassifiedNames.spaRootComponent}, pathMatch: 'full' },
        { path: '**', redirectTo: '/' },
      `
    ),
  ];

  const recorder = tree.beginUpdate(FilePaths.appRoutingModule);

  for (const change of changes) {
    if (change instanceof InsertChange) {
      recorder.insertLeft(change.pos, change.toAdd);
    }
  }

  tree.commitUpdate(recorder);
};

const editAppModule = (tree: Tree): void => {
  const changes = addDeclarationToModule(
    createSourceFile(FilePaths.appModule, tree),
    FilePaths.appModule,
    ClassifiedNames.spaRootComponent,
    spaRootComponentRelativePath(FilePaths.appModule)
  );

  const recorder = tree.beginUpdate(FilePaths.appModule);

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
      'Failed to find environment object literal expression'
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
