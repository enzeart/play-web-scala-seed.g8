import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyStandardTemplates } from '../utils/files';

export function graphql(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    return mergeWith(templateSources)(tree, _context);
  };
}
