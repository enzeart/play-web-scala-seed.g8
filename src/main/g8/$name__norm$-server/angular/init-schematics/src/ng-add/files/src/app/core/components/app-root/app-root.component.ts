import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

const appRedirectRouteQueryParam = 'app_redirect_route';

@Component({
  selector: 'app-app-root',
  templateUrl: './app-root.component.html',
  styleUrls: ['./app-root.component.css']
})
export class AppRootComponent implements OnInit {

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.followRedirectRoute();
  }

  followRedirectRoute(): void {
    const { queryParams } = this.activatedRoute.snapshot;
    const redirectRoute = queryParams[appRedirectRouteQueryParam];
    if (redirectRoute) {
      this.router.navigateByUrl(redirectRoute);
    }
  }
}
