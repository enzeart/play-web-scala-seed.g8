import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { NodePackageInstallTask } from '@angular-devkit/schematics/tasks';
import { applyStandardTemplates } from '../utils/files';

export function core(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    _context.addTask(new NodePackageInstallTask({ packageName: 'prettier' }));
    return mergeWith(templateSources)(tree, _context);
  };
}
