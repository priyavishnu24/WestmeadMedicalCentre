import { Component, OnInit, ViewChildren, QueryList, ViewEncapsulation } from '@angular/core';
import { AppointmentDTO } from '../models/AppointmentDTO';
import { UserDTO } from '../models/UserDTO';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../user.service';
import { TimeDTO } from '../models/TimeDTO';
import { session_user_key, session_user_type } from '../Constants';
import {MatDialog} from '@angular/material/dialog';
import { BookingTermsAndConditonsDialogComponent } from '../booking-terms-and-conditons-dialog/booking-terms-and-conditons-dialog.component';
import { Router } from "@angular/router";

@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppointmentComponent implements OnInit {

  selectedDoctor:UserDTO;
  selectedDate: Date;
  selectedTime: TimeDTO;
  reason: string;
  agreeCB: boolean;
  isAdmin: boolean;
  patientId: string;
  doctors:MatTableDataSource<UserDTO>;
  unModifiableDoctors:UserDTO[]=[];
  doctorColumns: string[] = ['firstName', 'lastName', 'age', 'gender', 'qualification', 'experience'];

  appointmentHistoryColumns: string[] = ['index', 'date', 'time', 'doctorName', 'reason', 'action'];
  appointments:MatTableDataSource<AppointmentDTO>;

  @ViewChildren(MatPaginator) paginators: QueryList<MatPaginator>;
  times:TimeDTO[]=[];
  user:UserDTO;
  constructor(private userService: UserService, private snackBar: MatSnackBar, public dialog: MatDialog,private router: Router) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem(session_user_key));

    this.selectedDoctor = new UserDTO();
    this.selectedDate = new Date();
    this.selectedTime = new TimeDTO();
    this.doctors = new MatTableDataSource<UserDTO>();
    this.isAdmin = (sessionStorage.getItem(session_user_type) == "admin");
   
    this.loadApprovedDoctors();
    this.loadAppointmentHistory();
    
  }

  ngAfterViewInit() {
    this.doctors.paginator = this.paginators.first;
   
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.appointments.filter = filterValue.trim().toLowerCase();
  }

  doctorApplyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.doctors.filter = filterValue.trim().toLowerCase();
  }

  private loadApprovedDoctors(){
    this.userService.getApprovedDoctors().subscribe(resp=>{
      this.unModifiableDoctors = resp;
      var date:string = this.getDateAsString(this.selectedDate);
      var doctors:UserDTO[] = [];
      this.unModifiableDoctors.forEach(doctorEle=>{
        if(doctorEle.availableTimes[date]){
          doctors.push(doctorEle);
        }
      });
      this.doctors.data = doctors;
    });
  }

  onSelectDoctor(doctor:UserDTO){
    this.selectedDoctor = doctor;   
    var date: string = this.getDateAsString(new Date());
    if(this.selectedDate){
      date = this.getDateAsString(this.selectedDate);
    }
    this.times = doctor.availableTimes[date];
    this.doctors.data.forEach(doctorEle => {
      if(doctorEle.emailId == doctor.emailId){
        doctorEle.highlighted = true
      } else {
        doctorEle.highlighted = false;
      }
    });

    
  }
  onSelectTime(time:TimeDTO, isSelected:boolean){
   this.times.forEach(timeEle => {
      timeEle.isSelected = false;
    });

    time.isSelected = isSelected;
    this.selectedTime = time;
    console.log(time.time.slice(0, 5));
  }

  public getDateAsString(date: Date):string{
    var month = "0", day = "0";
    if((date.getMonth() + 1) < 10){
      month += (date.getMonth()+1);
    } else {
      month = (date.getMonth() + 1).toString();
    }

    if((date.getDate()) < 10){
      day += (date.getDate());
    } else {
      day = (date.getDate()).toString();
    }
    return date.getFullYear() + "-" + month + "-" + day;
  }

  onChangeDate(){
    /**get the available time for this date for selected doctor*/
    console.log(this.selectedDate);
    this.times = this.selectedDoctor.availableTimes[this.getDateAsString(this.selectedDate)];

    /**Filter the doctors who are available for the selected date*/
    var date:string = this.getDateAsString(this.selectedDate);
    var doctors:UserDTO[] = [];
    this.unModifiableDoctors.forEach(doctorEle=>{
      if(doctorEle.availableTimes[date]){
        doctors.push(doctorEle);
      }
    });
    this.doctors.data = doctors;
    
    /**clean the selected time when date changes */
    this.selectedTime = new TimeDTO();
  }

  bookAppointment(){
    if(this.validateBookAppointment()){
      var patientEmailId;
      var adminId;
      var userType;
      if(this.isAdmin){
        patientEmailId = this.patientId;
        adminId = this.user.emailId;
        userType = "admin";
      } else {
        patientEmailId = this.user.emailId;
        userType = "patient";
      }
      this.userService.bookAppointment(patientEmailId, this.selectedDoctor.emailId, this.getDateAsString(this.selectedDate), this.selectedTime.time, this.reason, userType, adminId).subscribe(resp=>{
        if(resp){
          this.showSnackBar("Appointment Booked Successfully");
          this.router.navigate(["/"]);
        }
      }, err=>{
        var error:string = err.error;
        this.showSnackBar(error.substring(error.indexOf(":") + 1));
      });
    }
  }

  private validateBookAppointment(): boolean{
    if(!this.selectedDoctor.emailId){
      this.showSnackBar("Please select a doctor to book appointment");
      return false;
    } else if(!this.selectedTime.time){
      this.showSnackBar("Please select a time to book appointment");
      return false;
    } else if(!this.reason){
      this.showSnackBar("Please tell us the purpose of visit in step 2");
      return false;
    } else if(!this.agreeCB){
      this.showSnackBar("Please agree the terms and conditons");
      return false;
    } else if(this.isAdmin && !this.patientId){
      this.showSnackBar("Please enter patient Id");
      return false;
    }

    return true;
  }

  private showSnackBar(msg1: string, msg2?: string){
    this.snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  openTermsAndConditions(){
    const dialogRef = this.dialog.open(BookingTermsAndConditonsDialogComponent, {
      width: '500px'
    });
  }

  public loadAppointmentHistory(){
    var patientEmailId;
    if(this.isAdmin){
      patientEmailId = this.patientId;
    } else {
      patientEmailId = this.user.emailId;
    }
    this.userService.getAppointments(patientEmailId, "patient").subscribe(resp=>{
      this.appointments =  new MatTableDataSource<AppointmentDTO>(resp);
      this.appointments.paginator = this.paginators.last;
    }, err=>{
        this.showSnackBar("Something went wrong!!");
    });
  }
  public getReadableDate(time: string){
    return time.substring(0, time.indexOf("T"));
  }

  public getReadableTime(time: string){
    var minutes = time.substring(time.indexOf("T") + 4, 16);
    var hour = time.substr(time.indexOf("T") + 1, 13);
    var ampm = " AM";
    var hourInt: number = parseInt(hour);
    if (hourInt> 11) {
      ampm = " PM";
      if(hourInt != 12)
        hourInt = hourInt - 12;
    }

    var displayTime;
    if(hourInt < 10){
      displayTime = "0" + hourInt + ":" + minutes + " " + ampm;
    } else {
      displayTime = hourInt + ":" + minutes + " " + ampm;
    }

    return displayTime;
  }

  isCancelAvailable(date: string){
    var today = new Date();
    var dateArr: string[] = date.split("-");
    if(today.getFullYear() > parseInt(dateArr[0])){
      return false;
    } else if(today.getFullYear() == parseInt(dateArr[0]) && (today.getMonth() + 1) > parseInt(dateArr[1])){
      return false;
    } else if(today.getFullYear() == parseInt(dateArr[0]) && (today.getMonth() + 1) == parseInt(dateArr[1]) && today.getDate() > parseInt(dateArr[2])){
      return false;
    } else {
      return true;
    }
  }

  onCancelAppointment(appointment: AppointmentDTO){
    this.userService.cancelAppointment(appointment.appointmentId).subscribe(resp=>{
      if(resp){
        this.refreshPageData();
        this.showSnackBar("Appointment cancelled Successfully");
      } else {
        this.showSnackBar("Appointment Cancellation failed");
      }
    }, err=>{
      this.showSnackBar("Something went wrong!!");
    })
  }

  private refreshPageData(){
      this.loadAppointmentHistory();
      this.loadApprovedDoctors();
  }
}
