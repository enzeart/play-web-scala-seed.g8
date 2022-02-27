import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  overwritePackageJson,
  writeWorkspaceConfiguration,
  parsePackageJson,
  readWorkspaceConfiguration,
} from '../utils/files';

export function core(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const packageJson = parsePackageJson(tree);
    const workspaceConfig = readWorkspaceConfiguration(tree);
    const project = workspaceConfig.defaultProject;

    packageJson.scripts.build = 'ng build --outputPath=../public';
    packageJson.scripts.prettier = 'npx prettier --write "src/**/*.ts"';

    workspaceConfig.projects[project].schematics[
      '@schematics/angular:component'
    ] = {
      displayBlock: true,
    };

    overwritePackageJson(tree, packageJson);
    writeWorkspaceConfiguration(workspaceConfig, tree);

    return mergeWith(templateSources)(tree, _context);
  };
}
