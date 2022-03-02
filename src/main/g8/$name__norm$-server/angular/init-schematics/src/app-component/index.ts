import { Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyTemplates, applyWithOverwrite } from '../util/files-utils';

export function appComponent(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    return applyWithOverwrite(applyTemplates())(tree, _context);
  };
}
