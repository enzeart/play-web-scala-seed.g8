import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';

const addAjaxRequestResolverHeaders = (
  request: HttpRequest<unknown>
): HttpRequest<unknown> => {
  return request.clone({
    headers: request.headers.set('X-Requested-With', 'XMLHttpRequest'),
  });
};

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const modifiedRequest = addAjaxRequestResolverHeaders(request);
    return next.handle(modifiedRequest);
  }
}
