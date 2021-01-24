import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { addProviderToModule } from '../utility/ast-utils';
import { InsertChange } from '../utility/change';
import { buildRelativePath } from '../utility/find-module';
import { applyStandardTemplates, createAppModuleSourceFile, FilePaths } from '../utils/files';

const httpInterceptorProvidersClassifiedName = 'httpInterceptorProviders';
const relativePathToHttpInterceptors = buildRelativePath(FilePaths.APP_MODULE, `/src/app/core/http-interceptors/`);

export function appInterceptor(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    const appModuleSourceFile = createAppModuleSourceFile(tree);
    const provideHttpInterceptors = addProviderToModule(
      appModuleSourceFile,
      FilePaths.APP_MODULE,
      httpInterceptorProvidersClassifiedName,
      relativePathToHttpInterceptors
    );

    const recorder = tree.beginUpdate(FilePaths.APP_MODULE);
    for (const change of provideHttpInterceptors) {
      if (change instanceof InsertChange) {
        recorder.insertLeft(change.pos, change.toAdd);
      }
    }
    tree.commitUpdate(recorder);

    return mergeWith(templateSources)(tree, _context);
  };
}
