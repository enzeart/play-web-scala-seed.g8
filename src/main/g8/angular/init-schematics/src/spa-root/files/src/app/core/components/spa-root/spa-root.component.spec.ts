import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpaRootComponent } from './spa-root.component';

describe('SpaRootComponent', () => {
  let component: SpaRootComponent;
  let fixture: ComponentFixture<SpaRootComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SpaRootComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpaRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
