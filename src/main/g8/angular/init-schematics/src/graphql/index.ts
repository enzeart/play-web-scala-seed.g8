import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyTemplates, overwritePackageJson, parsePackageJson } from '../utils/files';

export function graphql(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const packageJson = parsePackageJson(tree);

    packageJson.scripts.gqlcodegen = 'graphql-codegen && npm run prettier';

    overwritePackageJson(tree, packageJson);

    return mergeWith(templateSources)(tree, _context);
  };
}
