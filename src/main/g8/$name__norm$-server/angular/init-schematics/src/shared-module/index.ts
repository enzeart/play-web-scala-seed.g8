import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  createAppModuleSourceFile,
  FilePaths,
} from '../utils/files';
import { addImportToModule } from '@schematics/angular/utility/ast-utils';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { InsertChange } from '@schematics/angular/utility/change';

const sharedModuleClassifiedName = 'SharedModule';

export function sharedModule(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const appModuleSourceFile = createAppModuleSourceFile(tree);
    const addSharedModuleImport = addImportToModule(
      appModuleSourceFile,
      FilePaths.APP_MODULE,
      sharedModuleClassifiedName,
      buildRelativePath(FilePaths.APP_MODULE, '/src/app/shared/shared.module')
    );

    const appModuleRecorder = tree.beginUpdate(FilePaths.APP_MODULE);

    for (const change of addSharedModuleImport) {
      if (change instanceof InsertChange) {
        appModuleRecorder.insertLeft(change.pos, change.toAdd);
      }
    }

    tree.commitUpdate(appModuleRecorder);

    return mergeWith(templateSources)(tree, _context);
  };
}
