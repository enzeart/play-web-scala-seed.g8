import {
  apply,
  MergeStrategy,
  mergeWith,
  Rule,
  SchematicContext, SchematicsException,
  template,
  Tree,
  url
} from '@angular-devkit/schematics';
import { RunSchematicTask } from '@angular-devkit/schematics/tasks';
import { JSONFile } from '@schematics/angular/utility/json-file';
import { addImportToModule, addProviderToModule } from '@schematics/angular/utility/ast-utils';
import * as ts from '@schematics/angular/third_party/github.com/Microsoft/TypeScript/lib/typescript';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { InsertChange } from '@schematics/angular/utility/change';

const appModulePath = '/src/app/app.module.ts';
const httpInterceptorsDirectoryPath = '/src/app/core/http-interceptors/';
const httpInterceptorProviders = 'httpInterceptorProviders';
const cookieService = 'CookieService';
const ngxCookieService = 'ngx-cookie-service';
const sharedModule = 'SharedModule';
const sharedModulePath = '/src/app/shared/shared.module';


// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function ngAdd(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSource = apply(url('./files'), [template({})]);
    const packageJsonFile = new JSONFile(tree, '/package.json');
    const workspaceConfigurationFile = new JSONFile(tree, '/angular.json');

    packageJsonFile.modify(['scripts', 'build'], 'ng build --outputPath=../public');
    packageJsonFile.modify(['scripts', 'prettier'], 'npx prettier --write "src/**/*.ts"');

    workspaceConfigurationFile.modify(['projects', _options.project, 'schematics', '@schematics/angular:component'], {
      displayBlock: true,
    });

    packageJsonFile.modify(['scripts', 'gqlcodegen'], 'graphql-codegen && npm run prettier');

    workspaceConfigurationFile.modify(['projects', _options.project, 'architect', 'serve', 'options'], {
      proxyConfig: './proxy.conf.js',
    });

    addProviderToAppModule(tree, httpInterceptorProviders, buildRelativePath(appModulePath, httpInterceptorsDirectoryPath));
    addProviderToAppModule(tree, cookieService, ngxCookieService);
    addImportToAppModule(tree, sharedModule, buildRelativePath(appModulePath, sharedModulePath));

    const environmentsTaskId = _context.addTask(new RunSchematicTask("@schematics/angular", "environments", {}));
    // _context.addTask(new RunSchematicTask("core", { project: _options.project}));
    // _context.addTask(new RunSchematicTask("proxy-config", { project: _options.project}));
    // _context.addTask(new RunSchematicTask("app-component", {}));
    _context.addTask(new RunSchematicTask("spa-root", {}), [environmentsTaskId]);
    // _context.addTask(new RunSchematicTask("app-interceptor", {}));
    // _context.addTask(new RunSchematicTask("graphql", {}));
    // _context.addTask(new RunSchematicTask("shared-module", {}));
    // _context.addTask(new RunSchematicTask("apollo-angular", "ng-add", {endpoint: "/api/graphql"}));
    return mergeWith(templateSource, MergeStrategy.Overwrite)(tree, _context);
  };
}

function createSourceFile(tree: Tree, path: string): ts.SourceFile {
  const buffer = tree.read(path);
  if (!buffer) throw new SchematicsException(`Failed to read file at path ${path}`);
  return ts.createSourceFile(path, buffer.toString(), ts.ScriptTarget.Latest, true);
}

function addProviderToAppModule(tree: Tree, classifiedName: string, importPath: string): void {
  const changes = addProviderToModule(createSourceFile(tree, appModulePath), appModulePath, classifiedName, importPath);
  const recorder = tree.beginUpdate(appModulePath);
  for (const change of changes) {
    if (change instanceof InsertChange) recorder.insertLeft(change.pos, change.toAdd);
  }
  tree.commitUpdate(recorder);
}

function addImportToAppModule(tree: Tree, classifiedName: string, importPath: string): void {
  const changes = addImportToModule(createSourceFile(tree, appModulePath), appModulePath, classifiedName, importPath);
  const recorder = tree.beginUpdate(appModulePath);
  for (const change of changes) {
    if (change instanceof InsertChange) recorder.insertLeft(change.pos, change.toAdd);
  }
  tree.commitUpdate(recorder);
}
