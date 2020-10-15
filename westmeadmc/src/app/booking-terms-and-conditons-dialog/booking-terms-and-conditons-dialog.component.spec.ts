import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookingTermsAndConditonsDialogComponent } from './booking-terms-and-conditons-dialog.component';

describe('BookingTermsAndConditonsDialogComponent', () => {
  let component: BookingTermsAndConditonsDialogComponent;
  let fixture: ComponentFixture<BookingTermsAndConditonsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BookingTermsAndConditonsDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookingTermsAndConditonsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
