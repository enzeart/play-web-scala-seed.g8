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
import * as ts from 'typescript';

export enum FilePaths {
  APP_MODULE = '/src/app/app.module.ts',
  APP_ROUTING_MODULE = '/src/app/app-routing.module.ts',
  PROXY_CONFIGURATION = '/proxy.conf.js',
  WORKSPACE_CONFIGURATION = '/angular.json',
}

export const parseWorkspaceConfig = (tree: Tree): any => {
  const workspaceConfigBuffer = tree.read(FilePaths.WORKSPACE_CONFIGURATION);
  if (!workspaceConfigBuffer) throw new SchematicsException('Could not find angular.json');
  return JSON.parse(workspaceConfigBuffer.toString('utf-8'));
};

export const createSourceFile = (path: string, tree: Tree): ts.SourceFile => {
  const buffer = tree.read(path);
  if (!buffer) throw new SchematicsException(`Could not find ${path}`);
  return ts.createSourceFile(path, buffer.toString('utf-8'), ts.ScriptTarget.Latest, true);
};

export const createAppModuleSourceFile = (tree: Tree): ts.SourceFile => {
  return createSourceFile(FilePaths.APP_MODULE, tree);
};

export const createAppRoutingModuleSourceFile = (tree: Tree): ts.SourceFile => {
  return createSourceFile(FilePaths.APP_ROUTING_MODULE, tree);
};

export const applyStandardTemplates = (options: any = {}): Source => apply(url('./files'), [template(options)]);

export function applyWithOverwrite(source: Source, rules: Rule[] = []): Rule {
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
}
