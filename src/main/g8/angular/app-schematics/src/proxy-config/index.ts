import {
  apply,
  mergeWith,
  Rule,
  SchematicContext,
  template,
  Tree,
  url,
  SchematicsException, chain
} from '@angular-devkit/schematics';

export function proxyConfig(_options: Schema): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const proxyConfigPath = './proxy.conf.js';
    const proxyConfigExists = tree.exists(proxyConfigPath);
    const proxyConfigRules = [];

    if (proxyConfigExists) {
      _context.logger.info('proxy.conf.js already exists in the workspace, skipping creation');
    } else {
      const proxyConfigTemplates = url('./files');
      const appliedProxyConfigTemplates = apply(proxyConfigTemplates, [template({})]);
      proxyConfigRules.push(mergeWith(appliedProxyConfigTemplates));
    }

    const workspaceConfigPath = './angular.json';
    const workspaceConfigBuffer = tree.read(workspaceConfigPath);
    if (!workspaceConfigBuffer) {
      throw new SchematicsException('angular.json is missing, this template can only be run in an Angular workspace');
    }

    const workspaceConfigJson = JSON.parse(workspaceConfigBuffer.toString('utf-8'));
    const project = _options.project || workspaceConfigJson?.defaultProject;
    if (!project) {
      throw new SchematicsException('No project provided, and no default project configured for the workspace');
    }

    const projectServeOptions = workspaceConfigJson?.projects[project]?.architect?.serve?.options;
    if (!projectServeOptions) {
      throw new SchematicsException(`Missing serve options object for project "${project}"`);
    }
    projectServeOptions.proxyConfig = proxyConfigPath;
    tree.overwrite(workspaceConfigPath, JSON.stringify(workspaceConfigJson, null, 2));

    return chain(proxyConfigRules)(tree, _context);
  };
}
