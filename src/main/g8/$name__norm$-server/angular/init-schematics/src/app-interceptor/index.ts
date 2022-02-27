import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import { addProviderToModule } from '@schematics/angular/utility/ast-utils';
import { InsertChange } from '@schematics/angular/utility/change';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import { applyTemplates, createSourceFile, FilePaths } from '../utils/files';

export function appInterceptor(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editAppModule(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const httpInterceptorProvidersClassifiedName = 'httpInterceptorProviders';

const editAppModule = (tree: Tree): void => {
  const appModuleSourceFile = createSourceFile(FilePaths.APP_MODULE, tree);
  const recorder = tree.beginUpdate(FilePaths.APP_MODULE);

  const changes = [
    ...addProviderToModule(
      appModuleSourceFile,
      FilePaths.APP_MODULE,
      httpInterceptorProvidersClassifiedName,
      buildRelativePath(
        FilePaths.APP_MODULE,
        `/src/app/core/http-interceptors/`
      )
    ),
  ];

  for (const change of changes) {
    if (change instanceof InsertChange) {
      recorder.insertLeft(change.pos, change.toAdd);
    }
  }

  tree.commitUpdate(recorder);
};
