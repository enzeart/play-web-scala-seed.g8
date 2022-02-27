import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  writePackageConfiguration,
  writeWorkspaceConfiguration,
  readPackageConfiguration,
  readWorkspaceConfiguration,
} from '../util/files-utils';

export function core(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editPackageConfiguration(tree);
    editWorkspaceConfiguration(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editPackageConfiguration = (tree: Tree): void => {
  const packageConfiguration = readPackageConfiguration(tree);

  packageConfiguration.scripts.build = 'ng build --outputPath=../public';
  packageConfiguration.scripts.prettier = 'npx prettier --write "src/**/*.ts"';

  writePackageConfiguration(packageConfiguration, tree);
};

const editWorkspaceConfiguration = (tree: Tree): void => {
  const workspaceConfiguration = readWorkspaceConfiguration(tree);

  workspaceConfiguration.projects[
    workspaceConfiguration.defaultProject
  ].schematics['@schematics/angular:component'] = {
    displayBlock: true,
  };

  writeWorkspaceConfiguration(workspaceConfiguration, tree);
};
