import {
  apply,
  mergeWith,
  Rule,
  SchematicContext,
  template,
  Tree,
  url,
} from "@angular-devkit/schematics";
import { addProviderToModule } from "../utility/ast-utils";
import { InsertChange } from "../utility/change";
import { buildRelativePath } from "../utility/find-module";
import { createAppModuleSourceFile, FilePaths } from "../utils/files";

export function appInterceptor(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = apply(url("./files"), [template({})]);
    const appModuleSourceFile = createAppModuleSourceFile(tree);
    const provideHttpInterceptors = addProviderToModule(
      appModuleSourceFile,
      FilePaths.APP_MODULE,
      "httpInterceptorProviders",
      buildRelativePath(
        FilePaths.APP_MODULE,
        `/src/app/core/http-interceptors/`
      )
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
