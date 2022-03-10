import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import { addProviderToModule } from '@schematics/angular/utility/ast-utils';
import { InsertChange } from '@schematics/angular/utility/change';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { applyTemplates, createSourceFile } from '../util/files-utils';
import * as FilePaths from '../util/file-paths';
import * as ClassifiedNames from '../util/classified-names';
import * as ImportPaths from '../util/import-paths';

export function appInterceptor(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editAppModule(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editAppModule = (tree: Tree): void => {
  const addProviders = (classifiedName: string, importPath: string) => {
    const changes = [
      ...addProviderToModule(
        createSourceFile(FilePaths.appModule, tree),
        FilePaths.appModule,
        classifiedName,
        importPath
      ),
    ];

    const recorder = tree.beginUpdate(FilePaths.appModule);

    for (const change of changes) {
      if (change instanceof InsertChange) {
        recorder.insertLeft(change.pos, change.toAdd);
      }
    }

    tree.commitUpdate(recorder);
  };

  addProviders(
    ClassifiedNames.httpInterceptorProviders,
    buildRelativePath(FilePaths.appModule, FilePaths.httpInterceptorsDirectory)
  );
  addProviders(ClassifiedNames.cookieService, ImportPaths.ngxCookieService);
};
