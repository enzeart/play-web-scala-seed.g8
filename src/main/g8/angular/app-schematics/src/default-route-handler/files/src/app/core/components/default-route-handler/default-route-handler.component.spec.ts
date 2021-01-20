import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefaultRouteHandlerComponent } from './default-route-handler.component';

describe('DefaultRouteHandlerComponent', () => {
  let component: DefaultRouteHandlerComponent;
  let fixture: ComponentFixture<DefaultRouteHandlerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DefaultRouteHandlerComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DefaultRouteHandlerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
