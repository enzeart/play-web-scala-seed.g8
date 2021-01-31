import { mergeWith, Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import { applyStandardTemplates } from '../utils/files';
import { NodePackageInstallTask } from '@angular-devkit/schematics/tasks';

export function graphql(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSources = applyStandardTemplates();
    _context.addTask(new NodePackageInstallTask({ packageName: '@graphql-codegen/cli' }));
    _context.addTask(new NodePackageInstallTask({ packageName: '@graphql-codegen/typescript' }));
    _context.addTask(new NodePackageInstallTask({ packageName: '@graphql-codegen/typescript-apollo-angular' }));
    _context.addTask(new NodePackageInstallTask({ packageName: '@graphql-codegen/typescript-operations' }));
    _context.addTask(new NodePackageInstallTask({ packageName: '@graphql-codegen/introspection' }));
    return mergeWith(templateSources)(tree, _context);
  };
}
