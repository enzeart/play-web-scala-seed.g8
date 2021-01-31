import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyStandardTemplates, FilePaths, parseWorkspaceConfig } from '../utils/files';
import { buildRelativePath } from '@schematics/angular/utility/find-module';

const relativePathToProxyConfiguration = buildRelativePath('/', FilePaths.PROXY_CONFIGURATION);

export function proxyConfig(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    const workspaceConfig = parseWorkspaceConfig(tree);
    const project = workspaceConfig.defaultProject;

    workspaceConfig.projects[project].architect.serve.options.proxyConfig = relativePathToProxyConfiguration;
    tree.overwrite(FilePaths.WORKSPACE_CONFIGURATION, JSON.stringify(workspaceConfig, null, 2));
    return mergeWith(templateSources)(tree, _context);
  };
}
