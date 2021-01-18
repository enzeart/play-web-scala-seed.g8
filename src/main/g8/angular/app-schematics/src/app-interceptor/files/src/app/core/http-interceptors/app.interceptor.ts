import { Injectable } from "@angular/core";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from "@angular/common/http";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  constructor() {}

  private static addAjaxRequestResolverHeaders(
    request: HttpRequest<unknown>
  ): HttpRequest<unknown> {
    return request.clone({
      headers: request.headers.set("X-Requested-With", "XMLHttpRequest"),
    });
  }

  private static reloadOnSessionTimeout<T>(source: Observable<T>) {
    return source.pipe(
      tap(
        () => {},
        (error) => {
          if (error instanceof HttpErrorResponse && error.status === 401)
            window.location.reload(true);
        }
      )
    );
  }

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const modifiedRequest = AppInterceptor.addAjaxRequestResolverHeaders(
      request
    );
    return next
      .handle(modifiedRequest)
      .pipe(AppInterceptor.reloadOnSessionTimeout);
  }
}
