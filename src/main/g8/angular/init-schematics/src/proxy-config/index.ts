import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  FilePaths,
  overwriteWorkspaceConfig,
  parseWorkspaceConfig,
} from '../utils/files';
import { buildRelativePath } from '@schematics/angular/utility/find-module';

const relativePathToProxyConfiguration = buildRelativePath(
  '/',
  FilePaths.PROXY_CONFIGURATION
);

export function proxyConfig(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    const workspaceConfig = parseWorkspaceConfig(tree);
    const project = workspaceConfig.defaultProject;

    workspaceConfig.projects[
      project
    ].architect.serve.options.proxyConfig = relativePathToProxyConfiguration;

    overwriteWorkspaceConfig(tree, workspaceConfig);

    return mergeWith(templateSources)(tree, _context);
  };
}
