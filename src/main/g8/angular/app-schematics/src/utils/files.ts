import { SchematicsException, Tree } from "@angular-devkit/schematics";
import * as ts from "typescript";

export enum FilePaths {
  APP_MODULE = "/src/app/app.module.ts",
  APP_ROUTING_MODULE = "/src/app/app-routing.module.ts",
  PROXY_CONFIGURATION = "/proxy.conf.js",
  WORKSPACE_CONFIGURATION = "/angular.json",
}

export const parseWorkspaceConfig = (tree: Tree): any => {
  const workspaceConfigBuffer = tree.read(FilePaths.WORKSPACE_CONFIGURATION);
  if (!workspaceConfigBuffer)
    throw new SchematicsException("Could not find angular.json");
  return JSON.parse(workspaceConfigBuffer.toString("utf-8"));
};

export const createAppModuleSourceFile = (tree: Tree): ts.SourceFile => {
  const appModuleBuffer = tree.read(FilePaths.APP_MODULE);
  if (!appModuleBuffer)
    throw new SchematicsException(`Could not find ${FilePaths.APP_MODULE}`);
  return ts.createSourceFile(
    FilePaths.APP_MODULE,
    appModuleBuffer.toString("utf-8"),
    ts.ScriptTarget.Latest,
    true
  );
};
