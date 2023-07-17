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
import * as FilePaths from './file-paths';

export const readWorkspaceConfiguration = (tree: Tree): any => {
  const buffer = tree.read(FilePaths.workspaceConfiguration);
  if (!buffer)
    throw new SchematicsException('Failed to read workspace configuration');
  return JSON.parse(buffer.toString('utf-8'));
};

export const writeWorkspaceConfiguration = (contents: any, tree: Tree) => {
  tree.overwrite(
    FilePaths.workspaceConfiguration,
    JSON.stringify(contents, null, 2),
  );
};

export const readPackageConfiguration = (tree: Tree) => {
  const packageJsonBuffer = tree.read(FilePaths.packageConfiguration);
  if (!packageJsonBuffer)
    throw new SchematicsException('Failed to read package definition.');
  return JSON.parse(packageJsonBuffer.toString('utf-8'));
};

export const writePackageConfiguration = (contents: any, tree: Tree) => {
  tree.overwrite(
    FilePaths.packageConfiguration,
    JSON.stringify(contents, null, 2),
  );
};

export const createSourceFile = (path: string, tree: Tree): ts.SourceFile => {
  const buffer = tree.read(path);
  if (!buffer)
    throw new SchematicsException(`Failed to read file at path "${path}"`);
  return ts.createSourceFile(
    path,
    buffer.toString('utf-8'),
    ts.ScriptTarget.Latest,
    true,
  );
};

export const applyTemplates = (options: any = {}): Source =>
  apply(url('./files'), [template(options)]);

export const applyWithOverwrite = (
  source: Source,
  rules: Rule[] = [],
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
      ]),
    );

    return rule(tree, _context);
  };
};
