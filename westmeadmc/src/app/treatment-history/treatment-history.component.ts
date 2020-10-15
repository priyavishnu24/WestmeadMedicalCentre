import { Component, OnInit, ViewEncapsulation  } from '@angular/core';
import { AppointmentDTO } from '../models/AppointmentDTO';
import { UserService } from '../user.service';
import { session_user_type, session_user_key, host } from '../Constants';
import { MatSnackBar } from '@angular/material/snack-bar';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-treatment-history',
  templateUrl: './treatment-history.component.html',
  styleUrls: ['./treatment-history.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TreatmentHistoryComponent implements OnInit {

  appointments:AppointmentDTO[] = [];
  isAdmin: boolean;
  patientId: string;
  constructor(private userService: UserService, private snackBar: MatSnackBar, private route:ActivatedRoute) { }

  ngOnInit(): void {
    var isDoctor = (sessionStorage.getItem(session_user_type) == "doctor");
    if(isDoctor){
      this.patientId = this.route.snapshot.paramMap.get('patientId');
    }

    this.isAdmin = (sessionStorage.getItem(session_user_type) == "admin");
    if(!isDoctor && !this.isAdmin){
      this.patientId = JSON.parse(sessionStorage.getItem(session_user_key)).emailId;
    }

    console.log("Patient Id : " + this.patientId);
    
    if(!this.isAdmin){
      this.onBlurLoadAppointments();
    }
    
  }

  private showSnackBar(msg1: string, msg2?: string){
    this.snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  public getDocsURL(appointmentId: string, docName : string): string{
    return host + "assets/images/treatmentDocs/" + appointmentId + "/" + docName;
  }

  onBlurLoadAppointments(){
      console.log("loading appointments");
      this.userService.getAppointments(this.patientId, 'patient').subscribe(resp=>{
        this.appointments = resp;
      }, err=>{
          this.showSnackBar("Something went wrong!!");
      })
  }

}
