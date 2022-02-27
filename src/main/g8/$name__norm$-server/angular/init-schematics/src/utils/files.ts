import {
  apply,
  forEach,
  mergeWith,
  Rule,
  SchematicContext,
  SchematicsException,
  Source,
  template,
  Tree,
  url,
} from '@angular-devkit/schematics';
import * as ts from '@schematics/angular/third_party/github.com/Microsoft/TypeScript/lib/typescript';

export enum FilePaths {
  APP_MODULE = '/src/app/app.module.ts',
  APP_ROUTING_MODULE = '/src/app/app-routing.module.ts',
  PROXY_CONFIGURATION = '/proxy.conf.js',
  WORKSPACE_CONFIGURATION = '/angular.json',
  PACKAGE_JSON = '/package.json',
  ENVIRONMENT = '/src/environments/environment.ts',
  ENVIRONMENT_PROD = '/src/environments/environment.prod.ts',
}

export const readWorkspaceConfiguration = (tree: Tree): any => {
  const buffer = tree.read(FilePaths.WORKSPACE_CONFIGURATION);
  if (!buffer)
    throw new SchematicsException('Failed to read workspace configuration');
  return JSON.parse(buffer.toString('utf-8'));
};

export const writeWorkspaceConfiguration = (contents: any, tree: Tree) => {
  tree.overwrite(
    FilePaths.WORKSPACE_CONFIGURATION,
    JSON.stringify(contents, null, 2)
  );
};

export const readPackageConfiguration = (tree: Tree) => {
  const packageJsonBuffer = tree.read(FilePaths.PACKAGE_JSON);
  if (!packageJsonBuffer)
    throw new SchematicsException('Failed to read package definition.');
  return JSON.parse(packageJsonBuffer.toString('utf-8'));
};

export const writePackageConfiguration = (contents: any, tree: Tree) => {
  tree.overwrite(FilePaths.PACKAGE_JSON, JSON.stringify(contents, null, 2));
};

export const createSourceFile = (path: string, tree: Tree): ts.SourceFile => {
  const buffer = tree.read(path);
  if (!buffer) throw new SchematicsException(`Could not find ${path}`);
  return ts.createSourceFile(
    path,
    buffer.toString('utf-8'),
    ts.ScriptTarget.Latest,
    true
  );
};

export const applyTemplates = (options: any = {}): Source =>
  apply(url('./files'), [template(options)]);

export const applyWithOverwrite = (
  source: Source,
  rules: Rule[] = []
): Rule => {
  return (tree: Tree, _context: SchematicContext) => {
    const rule = mergeWith(
      apply(source, [
        ...rules,
        forEach((fileEntry) => {
          if (tree.exists(fileEntry.path)) {
            tree.overwrite(fileEntry.path, fileEntry.content);
            return null;
          }
          return fileEntry;
        }),
      ])
    );

    return rule(tree, _context);
  };
};
