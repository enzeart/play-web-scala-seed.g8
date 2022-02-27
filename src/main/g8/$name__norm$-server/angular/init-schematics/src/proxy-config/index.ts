import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  FilePaths,
  writeWorkspaceConfiguration,
  readWorkspaceConfiguration,
} from '../utils/files';
import { buildRelativePath } from '@schematics/angular/utility/find-module';

export function proxyConfig(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editWorkspaceConfiguration(tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editWorkspaceConfiguration = (tree: Tree): void => {
  const workspaceConfiguration = readWorkspaceConfiguration(tree);

  workspaceConfiguration.projects[
    workspaceConfiguration.defaultProject
  ].architect.serve.options = {
    proxyConfig: buildRelativePath('/', FilePaths.PROXY_CONFIGURATION),
  };

  writeWorkspaceConfiguration(workspaceConfiguration, tree);
};
