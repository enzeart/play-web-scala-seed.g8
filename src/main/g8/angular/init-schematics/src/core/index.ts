import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  overwritePackageJson,
  overwriteWorkspaceConfig,
  parsePackageJson,
  parseWorkspaceConfig,
} from '../utils/files';

export function core(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const packageJson = parsePackageJson(tree);
    const workspaceConfig = parseWorkspaceConfig(tree);
    const project = workspaceConfig.defaultProject;

    packageJson.scripts.build = 'ng build --outputPath=../public';
    packageJson.scripts.prettier = 'npx prettier --write "src/**/*.ts"';

    workspaceConfig.projects[project].schematics[
      '@schematics/angular:component'
    ] = {
      displayBlock: true,
    };

    overwritePackageJson(tree, packageJson);
    overwriteWorkspaceConfig(tree, workspaceConfig);

    return mergeWith(templateSources)(tree, _context);
  };
}
