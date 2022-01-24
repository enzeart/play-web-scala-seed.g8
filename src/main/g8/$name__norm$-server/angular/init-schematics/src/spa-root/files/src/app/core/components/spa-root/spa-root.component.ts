import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-spa-root',
  templateUrl: './spa-root.component.html',
  styleUrls: ['./spa-root.component.css'],
})
export class SpaRootComponent implements OnInit {
  constructor(private activatedRoute: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.followRedirectRoute();
  }

  followRedirectRoute(): void {
    const { queryParams } = this.activatedRoute.snapshot;
    const redirectRoute = queryParams[environment.redirectRouteQueryParam];
    if (redirectRoute) {
      this.router.navigateByUrl(redirectRoute);
    }
  }
}
