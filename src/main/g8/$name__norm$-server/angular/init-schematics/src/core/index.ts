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
} from '../utils/files';

export function core(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const packageJson = readPackageConfiguration(tree);
    const workspaceConfig = readWorkspaceConfiguration(tree);
    const project = workspaceConfig.defaultProject;

    packageJson.scripts.build = 'ng build --outputPath=../public';
    packageJson.scripts.prettier = 'npx prettier --write "src/**/*.ts"';

    workspaceConfig.projects[project].schematics[
      '@schematics/angular:component'
    ] = {
      displayBlock: true,
    };

    writePackageConfiguration(packageJson, tree);
    writeWorkspaceConfiguration(workspaceConfig, tree);

    return mergeWith(templateSources)(tree, _context);
  };
}
