import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyTemplates, overwritePackageJson, parsePackageJson } from '../utils/files';

export function core(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const packageJson = parsePackageJson(tree);

    packageJson.scripts.build = 'ng build --outputPath=../public';
    packageJson.scripts.prettier = 'npx prettier --write "src/**/*.ts"';

    overwritePackageJson(tree, packageJson);

    return mergeWith(templateSources)(tree, _context);
  };
}
