import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { NgImageSliderModule } from 'ng-image-slider';
import {MatDialogModule} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import {MatDividerModule} from '@angular/material/divider';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import { HttpClientModule } from '@angular/common/http';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { HashLocationStrategy, LocationStrategy  } from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatSortModule} from '@angular/material/sort';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MenuComponent } from './menu/menu.component';
import { HealthTipsComponent } from './health-tips/health-tips.component';
import { CenterPaneComponent } from './center-pane/center-pane.component';
import { RightPaneComponent } from './right-pane/right-pane.component';
import { LeftPaneComponent } from './left-pane/left-pane.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { HomeComponent } from './home/home.component';
import { AppointmentComponent } from './appointment/appointment.component';
import { TreatmentHistoryComponent } from './treatment-history/treatment-history.component';
import { ManageAccountComponent } from './manage-account/manage-account.component';
import { AdminApprovalComponent } from './admin-approval/admin-approval.component';
import { BookingTermsAndConditonsDialogComponent } from './booking-terms-and-conditons-dialog/booking-terms-and-conditons-dialog.component';
import { DoctorAvailabilityComponent } from './doctor-availability/doctor-availability.component';
import { ViewAppointmentComponent } from './view-appointment/view-appointment.component';
import { AddTreatmentComponent } from './add-treatement/add-treatment.component';
import { OurDoctorsComponent } from './our-doctors/our-doctors.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    HealthTipsComponent,
    CenterPaneComponent,
    RightPaneComponent,
    LeftPaneComponent,
    LoginComponent,
    RegistrationComponent,
    AboutUsComponent,
    HomeComponent,
    AppointmentComponent,
    TreatmentHistoryComponent,
    ManageAccountComponent,
    AdminApprovalComponent,
    BookingTermsAndConditonsDialogComponent,
    DoctorAvailabilityComponent,
    ViewAppointmentComponent,
    AddTreatmentComponent,
    OurDoctorsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    NgImageSliderModule,
    MatDialogModule,
    MatFormFieldModule,
    FormsModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatStepperModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    HttpClientModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatSortModule
  ],
  providers: [{provide : LocationStrategy , useClass: HashLocationStrategy}],
  bootstrap: [AppComponent]
})
export class AppModule { }
