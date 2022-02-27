import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import { applyTemplates, createSourceFile, FilePaths } from '../utils/files';
import { addImportToModule } from '@schematics/angular/utility/ast-utils';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { InsertChange } from '@schematics/angular/utility/change';

export function sharedModule(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editAppModule(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const sharedModuleClassifiedName = 'SharedModule';

const editAppModule = (tree: Tree): void => {
  const appModuleSourceFile = createSourceFile(FilePaths.APP_MODULE, tree);

  const changes = [
    ...addImportToModule(
      appModuleSourceFile,
      FilePaths.APP_MODULE,
      sharedModuleClassifiedName,
      buildRelativePath(FilePaths.APP_MODULE, '/src/app/shared/shared.module')
    ),
  ];

  const recorder = tree.beginUpdate(FilePaths.APP_MODULE);

  for (const change of changes) {
    if (change instanceof InsertChange) {
      recorder.insertLeft(change.pos, change.toAdd);
    }
  }

  tree.commitUpdate(recorder);
};
