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

export function appInterceptor(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editAppModule(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editAppModule = (tree: Tree): void => {
  const appModuleSourceFile = createSourceFile(FilePaths.appModule, tree);

  const changes = [
    ...addProviderToModule(
      appModuleSourceFile,
      FilePaths.appModule,
      ClassifiedNames.httpInterceptorProviders,
      buildRelativePath(
        FilePaths.appModule,
        FilePaths.httpInterceptorsDirectory
      )
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
