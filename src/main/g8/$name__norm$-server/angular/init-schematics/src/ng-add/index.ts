import {
  apply,
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
  _insertImport, ClassifiedNames, FilePaths
} from '../util';


// You don't have to export the function as default. You can also have more than one rule factory
// per file.
export function ngAdd(_options: any): Rule {
  return (tree: Tree, _context: SchematicContext) => {
    const templateSource = apply(url('./files'), [template({})]);
    const packageJsonFile = new JSONFile(tree, '/package.json');
    const workspaceConfigurationFile = new JSONFile(tree, '/angular.json');

    packageJsonFile.modify(
      ['scripts', 'build'],
      'ng build --output-path=../public',
    );
    packageJsonFile.modify(
      ['scripts', 'prettier'],
      'npx prettier --write "src/**/*.ts"',
    );

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

    packageJsonFile.modify(
      ['scripts', 'gqlcodegen'],
      'graphql-codegen && npm run prettier',
    );

    workspaceConfigurationFile.modify(
      ['projects', _options.project, 'architect', 'serve', 'options'],
      {
        proxyConfig: './proxy.conf.js',
      },
    );

    _addProviderToModule(
      tree,
      FilePaths.AppModule,
      'httpInterceptorProviders',
      buildRelativePath(FilePaths.AppModule, '/src/app/core/http-interceptors/'),
    );
    _addProviderToModule(
      tree,
      FilePaths.AppModule,
      'CookieService',
      'ngx-cookie-service',
    );
    _addImportToModule(
      tree,
      FilePaths.AppModule,
      'SharedModule',
      buildRelativePath(FilePaths.AppModule, '/src/app/shared/shared.module'),
    );
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

    return mergeWith(templateSource, MergeStrategy.Overwrite)(tree, _context);
  };
}
