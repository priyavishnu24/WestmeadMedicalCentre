import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CenterPaneComponent } from './center-pane.component';

describe('CenterPaneComponent', () => {
  let component: CenterPaneComponent;
  let fixture: ComponentFixture<CenterPaneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CenterPaneComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CenterPaneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
