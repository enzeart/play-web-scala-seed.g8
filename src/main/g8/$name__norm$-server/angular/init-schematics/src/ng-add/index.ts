import {
  apply,
  MergeStrategy,
  mergeWith,
  Rule,
  SchematicContext,
  template,
  Tree,
  url
} from '@angular-devkit/schematics';
import { RunSchematicTask } from '@angular-devkit/schematics/tasks';
import { JSONFile } from '@schematics/angular/utility/json-file';


// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function ngAdd(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSource = apply(url('./files'), [template({})]);
    const packageJsonFile = new JSONFile(tree, '/package.json');

    packageJsonFile.modify(['scripts', 'gqlcodegen'], 'graphql-codegen && npm run prettier');

    const environmentsTaskId = _context.addTask(new RunSchematicTask("@schematics/angular", "environments", {}));
    _context.addTask(new RunSchematicTask("core", { project: _options.project}));
    _context.addTask(new RunSchematicTask("proxy-config", { project: _options.project}));
    // _context.addTask(new RunSchematicTask("app-component", {}));
    _context.addTask(new RunSchematicTask("spa-root", {}), [environmentsTaskId]);
    _context.addTask(new RunSchematicTask("app-interceptor", {}));
    // _context.addTask(new RunSchematicTask("graphql", {}));
    _context.addTask(new RunSchematicTask("shared-module", {}));
    // _context.addTask(new RunSchematicTask("apollo-angular", "ng-add", {endpoint: "/api/graphql"}));
    return mergeWith(templateSource, MergeStrategy.Overwrite)(tree, _context);
  };
}
