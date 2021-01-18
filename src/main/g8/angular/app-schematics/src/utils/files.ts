import { SchematicsException, Tree } from "@angular-devkit/schematics";

export enum FilePaths {
  APP_MODULE = '/src/app/app.module.ts',
  APP_ROUTING_MODULE = '/src/app/app-routing.module.ts',
  PROXY_CONFIGURATION = '/proxy.conf.js',
  WORKSPACE_CONFIGURATION = '/angular.json'
}

export const parseWorkspaceConfig = (tree: Tree): any => {
  const workspaceConfigBuffer = tree.read(FilePaths.WORKSPACE_CONFIGURATION);
  if (!workspaceConfigBuffer) throw new SchematicsException("Could not find angular.json");
  return JSON.parse(workspaceConfigBuffer.toString('utf-8'));
}
