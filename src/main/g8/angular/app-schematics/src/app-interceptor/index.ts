import {
  apply,
  mergeWith,
  move,
  Rule,
  SchematicContext,
  SchematicsException,
  template,
  Tree,
  url
} from '@angular-devkit/schematics';
import * as ts from 'typescript';
import {addProviderToModule} from "../utility/ast-utils";
import {InsertChange} from "../utility/change";
import { buildRelativePath } from "../utility/find-module";

export function appInterceptor(_options: AppInterceptorSchema): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const workspaceConfigPath = './angular.json';
    const workspaceConfigBuffer = tree.read(workspaceConfigPath);
    if (!workspaceConfigBuffer) {
      throw new SchematicsException('angular.json is missing, this template can only be run in an Angular workspace');
    }

    const workspaceConfigJson = JSON.parse(workspaceConfigBuffer.toString('utf-8'));
    const project = _options.project || workspaceConfigJson?.defaultProject;
    if (!project) {
      throw new SchematicsException('No project provided, and no default project configured for the workspace');
    }

    const root = workspaceConfigJson.projects[project].root;
    const sourceRoot = workspaceConfigJson.projects[project].sourceRoot;
    const path = `${root}/${sourceRoot}`;
    const templateSources = apply(url('./files'), [template({}), move(path)]);

    // TODO: Build path from workspace configuration
    const appModulePath = '/src/app/app.module.ts';
    const appModuleBuffer = tree.read(appModulePath);
    if (appModuleBuffer === null) throw new SchematicsException('');
    const appModuleText = appModuleBuffer.toString('utf-8');
    const appModuleSourceFile = ts.createSourceFile(appModulePath, appModuleText, ts.ScriptTarget.Latest, true);
    const provideHttpInterceptors = addProviderToModule(appModuleSourceFile, appModulePath, 'httpInterceptorProviders', buildRelativePath(`/${appModulePath}`, `/${path}/http-interceptors`));
    const changes = [...provideHttpInterceptors];
    const recorder = tree.beginUpdate(appModulePath);
    changes.forEach(change => {
      if (change instanceof InsertChange) {
        recorder.insertLeft(change.pos, change.toAdd);
      }
    })
    tree.commitUpdate(recorder);


    return mergeWith(templateSources)(tree, _context);
  };
}
