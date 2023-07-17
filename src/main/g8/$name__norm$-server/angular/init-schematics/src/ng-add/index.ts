import { Rule, SchematicContext, Tree } from '@angular-devkit/schematics';
import {RunSchematicTask} from '@angular-devkit/schematics/tasks';


// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function ngAdd(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const environmentsTask = _context.addTask(new RunSchematicTask("@schematics/angular", "environments", {}));
    _context.addTask(new RunSchematicTask("core", { project: _options.project}));
    _context.addTask(new RunSchematicTask("proxy-config", { project: _options.project}));
    _context.addTask(new RunSchematicTask("app-component", {}));
    _context.addTask(new RunSchematicTask("spa-root", {}), [environmentsTask]);
    _context.addTask(new RunSchematicTask("app-interceptor", {}));
    _context.addTask(new RunSchematicTask("graphql", {}));
    _context.addTask(new RunSchematicTask("shared-module", {}));
    // _context.addTask(new RunSchematicTask("apollo-angular", "ng-add", {endpoint: "/api/graphql"}));
    return tree;
  };
}
