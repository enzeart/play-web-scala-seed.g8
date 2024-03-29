import {
  apply,
  chain,
  MergeStrategy,
  mergeWith,
  Rule,
  SchematicContext,
  template,
  Tree,
  url,
} from '@angular-devkit/schematics';
import { JSONFile } from '@schematics/angular/utility/json-file';
import { buildRelativePath } from '@schematics/angular/utility/find-module';
import {
  _addDeclarationToModule,
  _addImportToModule,
  _addProviderToModule,
  _addRouteDeclarationToModule,
  _insertImport,
  ClassifiedNames,
  FilePaths,
} from '../util';
import { getPackageJsonDependency } from '@schematics/angular/utility/dependencies';
import minVersion from 'semver/ranges/min-version';
import { addDependency } from '@schematics/angular/utility/dependency';

// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function ngAdd(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSource = apply(url('./files'), [template({})]);
    const packageJsonFile = new JSONFile(tree, '/package.json');
    const workspaceConfigurationFile = new JSONFile(tree, '/angular.json');
    const rules = [mergeWith(templateSource, MergeStrategy.Overwrite)];
    const angularCoreDependency = getPackageJsonDependency(
      tree,
      '@angular/core',
    );
    const angularCoreSemver = angularCoreDependency
      ? minVersion(angularCoreDependency.version)
      : null;

    // Add a script for building the UI
    packageJsonFile.modify(
      ['scripts', 'build'],
      'ng build --output-path=../public',
    );

    // Add a script for running prettier
    packageJsonFile.modify(
      ['scripts', 'prettier'],
      'npx prettier --write "src/**/*.ts"',
    );

    /**
     * Components generated with the Angular CLI will
     * have a default CSS rule to make them display as block-level elements.
     */
    workspaceConfigurationFile.modify(
      [
        'projects',
        _options.project,
        'schematics',
        '@schematics/angular:component',
      ],
      {
        displayBlock: true,
      },
    );

    // Add utility script to run GraphQL code generation and prettier
    packageJsonFile.modify(
      ['scripts', 'gqlcodegen'],
      'graphql-codegen && npm run prettier',
    );

    // Add proxy configurations to the workspace configurations
    workspaceConfigurationFile.modify(
      ['projects', _options.project, 'architect', 'serve', 'options'],
      {
        proxyConfig: './proxy.conf.js',
      },
    );

    // Add HTTP Interceptor providers to AppModule along with their dependencies
    _addProviderToModule(
      tree,
      FilePaths.AppModule,
      'httpInterceptorProviders',
      buildRelativePath(
        FilePaths.AppModule,
        '/src/app/core/http-interceptors/',
      ),
    );
    _addProviderToModule(
      tree,
      FilePaths.AppModule,
      'CookieService',
      'ngx-cookie-service',
    );

    if (angularCoreSemver)
      rules.push(
        addDependency('ngx-cookie-service', `^${angularCoreSemver.major}`),
      );

    // Import SharedModule into AppModule
    _addImportToModule(
      tree,
      FilePaths.AppModule,
      'SharedModule',
      buildRelativePath(FilePaths.AppModule, '/src/app/shared/shared.module'),
    );

    // Configure routing for AppRootComponent, and set the default route.
    _addDeclarationToModule(
      tree,
      FilePaths.AppModule,
      ClassifiedNames.AppRootComponent,
      buildRelativePath(FilePaths.AppModule, FilePaths.AppRootComponent),
    );
    _insertImport(
      tree,
      FilePaths.AppRoutingModule,
      ClassifiedNames.AppRootComponent,
      buildRelativePath(FilePaths.AppRoutingModule, FilePaths.AppRootComponent),
    );
    _addRouteDeclarationToModule(
      tree,
      FilePaths.AppRoutingModule,
      `
        { path: '', component: ${ClassifiedNames.AppRootComponent}, pathMatch: 'full' },
        { path: '**', redirectTo: '/' },
      `,
    );

    return chain(rules)(tree, _context);
  };
}
