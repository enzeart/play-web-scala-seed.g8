import { Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyStandardTemplates, applyWithOverwrite } from '../utils/files';

export function appComponent(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    return applyWithOverwrite(templateSources)(tree, _context);
  };
}
