import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import { applyTemplates, createSourceFile } from '../util/files-utils';
import { addImportToModule } from '@schematics/angular/utility/ast-utils';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { InsertChange } from '@schematics/angular/utility/change';
import * as FilePaths from '../util/file-paths';
import * as ImportPaths from '../util/import-paths';
import * as ClassifiedNames from '../util/classified-names';

export function sharedModule(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editAppModule(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editAppModule = (tree: Tree): void => {
  const appModuleSourceFile = createSourceFile(FilePaths.appModule, tree);

  const changes = [
    ...addImportToModule(
      appModuleSourceFile,
      FilePaths.appModule,
      ClassifiedNames.sharedModule,
      buildRelativePath(FilePaths.appModule, ImportPaths.sharedModule)
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
