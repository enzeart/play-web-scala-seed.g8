import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AppInterceptor implements HttpInterceptor {
  constructor(private cookieService: CookieService) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const modifiedRequest = request.clone({
      headers: request.headers
        .set('X-Requested-With', 'XMLHttpRequest')
        .set('pac4jCsrfToken', this.cookieService.get('pac4jCsrfToken')),
    });

    return next.handle(modifiedRequest);
  }
}
