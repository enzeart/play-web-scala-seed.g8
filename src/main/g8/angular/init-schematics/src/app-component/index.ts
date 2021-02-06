import { Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyTemplates, applyWithOverwrite } from '../utils/files';

export function appComponent(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyTemplates();
    return applyWithOverwrite(templateSources)(tree, _context);
  };
}
