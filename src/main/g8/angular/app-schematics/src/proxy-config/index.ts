import { apply, mergeWith, Rule, SchematicContext, template, Tree, url } from '@angular-devkit/schematics';
import { FilePaths, parseWorkspaceConfig } from "../utils/files";
import { buildRelativePath } from "../utility/find-module";

export function proxyConfig(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {

    const workspaceConfig = parseWorkspaceConfig(tree);
    const project = workspaceConfig.defaultProject;
    workspaceConfig.projects[project].architect.serve.options.proxyConfig = buildRelativePath('/', FilePaths.PROXY_CONFIGURATION);
    tree.overwrite(FilePaths.WORKSPACE_CONFIGURATION, JSON.stringify(workspaceConfig, null, 2));

    return !tree.exists(FilePaths.PROXY_CONFIGURATION) ? mergeWith(apply(url('./files'), [template({})]))(tree, _context) : tree;
  };
}
