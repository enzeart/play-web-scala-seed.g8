import { SchematicsException, Tree } from '@angular-devkit/schematics';
import * as ts from '@schematics/angular/third_party/github.com/Microsoft/TypeScript/lib/typescript';
import {
  addDeclarationToModule,
  addImportToModule,
  addProviderToModule, addRouteDeclarationToModule,
  insertImport
} from '@schematics/angular/utility/ast-utils';
import { Change, InsertChange } from '@schematics/angular/utility/change';

export enum FilePaths {
  AppModule = '/src/app/app.module.ts',
  AppRoutingModule = '/src/app/app-routing.module.ts',
  AppRootComponent = '/src/app/core/components/app-root/app-root.component'
}

export enum ClassifiedNames {
  AppRootComponent = 'AppRootComponent'
}

export function createSourceFile(tree: Tree, path: string): ts.SourceFile {
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

export function _addProviderToModule(
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

export function _addImportToModule(
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

export function _addDeclarationToModule(
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

export function _insertImport(
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

export function _addRouteDeclarationToModule(
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

export function recordChanges(tree: Tree, path: string, ...changes: Change[]): void {
  const recorder = tree.beginUpdate(path);
  for (const change of changes) {
    if (change instanceof InsertChange)
      recorder.insertLeft(change.pos, change.toAdd);
  }
  tree.commitUpdate(recorder);
}
