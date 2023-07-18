import {
  apply,
  MergeStrategy,
  mergeWith,
  Rule,
  SchematicContext,
  SchematicsException,
  template,
  Tree,
  url,
} from '@angular-devkit/schematics';
import { JSONFile } from '@schematics/angular/utility/json-file';
import {
  addDeclarationToModule,
  addImportToModule,
  addProviderToModule,
  addRouteDeclarationToModule,
  insertImport,
} from '@schematics/angular/utility/ast-utils';
import * as ts from '@schematics/angular/third_party/github.com/Microsoft/TypeScript/lib/typescript';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { Change, InsertChange } from '@schematics/angular/utility/change';

const appModulePath = '/src/app/app.module.ts';
const appRoutingModulePath = '/src/app/app-routing.module.ts';
const appRootComponent = 'AppRootComponent';
const appRootComponentPath =
  '/src/app/core/components/app-root/app-root.component';

// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function ngAdd(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSource = apply(url('./files'), [template({})]);
    const packageJsonFile = new JSONFile(tree, '/package.json');
    const workspaceConfigurationFile = new JSONFile(tree, '/angular.json');

    packageJsonFile.modify(
      ['scripts', 'build'],
      'ng build --outputPath=../public',
    );
    packageJsonFile.modify(
      ['scripts', 'prettier'],
      'npx prettier --write "src/**/*.ts"',
    );

    workspaceConfigurationFile.modify(
      [
        'projects',
        _options.project,
        'schematics',
        '@schematics/angular:component',
      ],
      {
        displayBlock: true,
      },
    );

    packageJsonFile.modify(
      ['scripts', 'gqlcodegen'],
      'graphql-codegen && npm run prettier',
    );

    workspaceConfigurationFile.modify(
      ['projects', _options.project, 'architect', 'serve', 'options'],
      {
        proxyConfig: './proxy.conf.js',
      },
    );

    _addProviderToModule(
      tree,
      appModulePath,
      'httpInterceptorProviders',
      buildRelativePath(appModulePath, '/src/app/core/http-interceptors/'),
    );
    _addProviderToModule(
      tree,
      appModulePath,
      'CookieService',
      'ngx-cookie-service',
    );
    _addImportToModule(
      tree,
      appModulePath,
      'SharedModule',
      buildRelativePath(appModulePath, '/src/app/shared/shared.module'),
    );
    _addDeclarationToModule(
      tree,
      appModulePath,
      appRootComponent,
      buildRelativePath(appModulePath, appRootComponentPath),
    );
    _insertImport(
      tree,
      appRoutingModulePath,
      appRootComponent,
      buildRelativePath(appRoutingModulePath, appRootComponentPath),
    );
    _addRouteDeclarationToModule(
      tree,
      appRoutingModulePath,
      `
        { path: '', component: ${appRootComponent}, pathMatch: 'full' },
        { path: '**', redirectTo: '/' },
      `,
    );

    return mergeWith(templateSource, MergeStrategy.Overwrite)(tree, _context);
  };
}

function createSourceFile(tree: Tree, path: string): ts.SourceFile {
  const buffer = tree.read(path);
  if (!buffer)
    throw new SchematicsException(`Failed to read file at path ${path}`);
  return ts.createSourceFile(
    path,
    buffer.toString(),
    ts.ScriptTarget.Latest,
    true,
  );
}

function _addProviderToModule(
  tree: Tree,
  modulePath: string,
  classifiedName: string,
  importPath: string,
): void {
  const source = createSourceFile(tree, modulePath);
  recordChanges(
    tree,
    modulePath,
    ...addProviderToModule(source, modulePath, classifiedName, importPath),
  );
}

function _addImportToModule(
  tree: Tree,
  modulePath: string,
  classifiedName: string,
  importPath: string,
): void {
  const source = createSourceFile(tree, modulePath);
  recordChanges(
    tree,
    modulePath,
    ...addImportToModule(source, modulePath, classifiedName, importPath),
  );
}

function _addDeclarationToModule(
  tree: Tree,
  modulePath: string,
  classifiedName: string,
  importPath: string,
): void {
  const source = createSourceFile(tree, modulePath);
  recordChanges(
    tree,
    modulePath,
    ...addDeclarationToModule(source, modulePath, classifiedName, importPath),
  );
}

function _insertImport(
  tree: Tree,
  filePath: string,
  classifiedName: string,
  importPath: string,
): void {
  const source = createSourceFile(tree, filePath);
  recordChanges(
    tree,
    filePath,
    insertImport(source, filePath, classifiedName, importPath),
  );
}

function _addRouteDeclarationToModule(
  tree: Tree,
  modulePath: string,
  routeLiteral: string,
): void {
  const source = createSourceFile(tree, modulePath);
  recordChanges(
    tree,
    modulePath,
    addRouteDeclarationToModule(source, modulePath, routeLiteral),
  );
}

function recordChanges(tree: Tree, path: string, ...changes: Change[]): void {
  const recorder = tree.beginUpdate(path);
  for (const change of changes) {
    if (change instanceof InsertChange)
      recorder.insertLeft(change.pos, change.toAdd);
  }
  tree.commitUpdate(recorder);
}
