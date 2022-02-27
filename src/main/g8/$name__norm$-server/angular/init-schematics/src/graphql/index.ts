import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  writePackageConfiguration,
  readPackageConfiguration,
} from '../util/files-utils';

export function graphql(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editPackageConfiguration(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editPackageConfiguration = (tree: Tree): void => {
  const packageConfiguration = readPackageConfiguration(tree);
  packageConfiguration.scripts.gqlcodegen =
    'graphql-codegen && npm run prettier';
  writePackageConfiguration(packageConfiguration, tree);
};
