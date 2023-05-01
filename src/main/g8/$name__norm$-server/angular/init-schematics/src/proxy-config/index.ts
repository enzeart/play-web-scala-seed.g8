import {
  mergeWith,
  Rule,
  SchematicContext,
  Tree,
} from '@angular-devkit/schematics';
import {
  applyTemplates,
  writeWorkspaceConfiguration,
  readWorkspaceConfiguration,
} from '../util/files-utils';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import * as FilePaths from '../util/file-paths';

export function proxyConfig(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    editWorkspaceConfiguration(_options.project, tree);
    return mergeWith(applyTemplates())(tree, _context);
  };
}

const editWorkspaceConfiguration = (project: string, tree: Tree): void => {
  const workspaceConfiguration = readWorkspaceConfiguration(tree);

  workspaceConfiguration.projects[project].architect.serve.options = {
    proxyConfig: buildRelativePath('/', FilePaths.proxyConfiguration),
  };

  writeWorkspaceConfiguration(workspaceConfiguration, tree);
};
