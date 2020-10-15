import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { AppointmentDTO } from '../models/AppointmentDTO';
import {MatTableDataSource} from '@angular/material/table';
import { UserService } from '../user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { session_user_key, session_user_type } from '../Constants';
import {MatPaginator} from '@angular/material/paginator';
import {MatDialog} from '@angular/material/dialog';
import { AddTreatmentComponent } from '../add-treatement/add-treatment.component';

@Component({
  selector: 'app-view-appointment',
  templateUrl: './view-appointment.component.html',
  styleUrls: ['./view-appointment.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ViewAppointmentComponent implements OnInit {

  displayedColumns: string[] = ['index', 'time', 'patientName', 'patientAge', 'gender','reason', "treatment"];
  selectedDate: Date;
  appointments: MatTableDataSource<AppointmentDTO>;
  doctorId: string;
  isAdmin: boolean;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(private userService: UserService, private snackBar: MatSnackBar, public dialog: MatDialog) { 
    this.appointments =  new MatTableDataSource<AppointmentDTO>();
  }

  ngOnInit(): void {
    this.selectedDate = new Date();
    this.isAdmin = (sessionStorage.getItem(session_user_type) == "admin");

    if(!this.isAdmin){
      this.doctorId = JSON.parse(sessionStorage.getItem(session_user_key)).emailId;
      this.userService.getAppointments(this.doctorId, "doctor", this.getDateAsString(this.selectedDate)).subscribe(resp=>{
        this.appointments =  new MatTableDataSource<AppointmentDTO>(resp);
        this.appointments.paginator = this.paginator;
        
        console.log( JSON.stringify(this.appointments.data));
      }, err=>{
          this.showSnackBar("Something went wrong");
      });
    }
  }

  ngAfterViewInit() {
  }

  onChangeDate(){
    console.log(this.selectedDate);
    this.userService.getAppointments(this.doctorId, "doctor", this.getDateAsString(this.selectedDate)).subscribe(resp=>{
      this.appointments =  new MatTableDataSource<AppointmentDTO>(resp);
      this.appointments.paginator = this.paginator;
      console.log( JSON.stringify(this.appointments.data));
    }, err=>{
        this.showSnackBar("Something went wrong");
    })
  }

  private showSnackBar(msg1: string, msg2?: string){
    this.snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  private getDateAsString(date: Date):string{
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

  public getReadableDate(time: string){
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

  public viewPatientHistory(appointment: AppointmentDTO){
    console.log(appointment);
   
  }

  public addTreatment(appointment: AppointmentDTO){
    console.log(appointment);
    const dialogRef = this.dialog.open(AddTreatmentComponent, {
      width: '700px',
      data: appointment
    });
  }
}
