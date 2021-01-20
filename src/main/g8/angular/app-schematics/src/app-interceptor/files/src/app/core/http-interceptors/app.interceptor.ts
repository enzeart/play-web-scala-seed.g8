import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

const addAjaxRequestResolverHeaders = (request: HttpRequest<unknown>): HttpRequest<unknown> => {
  return request.clone({
    headers: request.headers.set('X-Requested-With', 'XMLHttpRequest'),
  });
};

const reloadOnSessionTimeout = (source: Observable<HttpEvent<unknown>>): Observable<HttpEvent<unknown>> => {
  return source.pipe(
    tap(
      () => {},
      (error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) window.location.reload(true);
      }
    )
  );
};

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const modifiedRequest = addAjaxRequestResolverHeaders(request);
    return next.handle(modifiedRequest).pipe(reloadOnSessionTimeout);
  }
}
