import { Component, OnInit,Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { UserService } from '../user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { session_user_key } from '../Constants';
import { AppointmentDTO } from '../models/AppointmentDTO';
import { UserDTO } from '../models/UserDTO';
import { TimeDTO } from '../models/TimeDTO';

@Component({
  selector: 'app-add-treatment',
  templateUrl: './add-treatment.component.html',
  styleUrls: ['./add-treatment.component.css']
})
export class AddTreatmentComponent implements OnInit {

  doctorComment: string;
  files: File[];
  revisit: string;
  revisitDate : Date;
  constructor(private dialogRef: MatDialogRef<AddTreatmentComponent>, @Inject(MAT_DIALOG_DATA) private appointment: AppointmentDTO, private userService: UserService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
  }

  onFileSelected(event) {
    this.files = event.target.files;
    console.log(this.files);
  }

  public saveTreatment(){
    if(this.validateRequest()){
      this.userService.saveTreatment(this.files, this.doctorComment, this.appointment.appointmentId).subscribe(resp=>{
          this.showSnackBar("Treatment added sucessfully!!");
          this.dialogRef.close();

          if(this.revisit){
            var doctor: UserDTO = JSON.parse(sessionStorage.getItem(session_user_key));
            var date = this.getDateAsString(this.revisitDate);
            var times:TimeDTO[] = doctor.availableTimes[date];
            var time: TimeDTO = times.find(time=>!time.appointmentId);
            this.userService.bookAppointment(this.appointment.patient.emailId, doctor.emailId, date, time.time, "revisit", "admin", doctor.emailId).subscribe(resp=>{
              if(resp){
                this.showSnackBar("Appointment Booked Successfully");
              } else {
                this.showSnackBar("Appointment Booked Failed");
              }
            }, err=>{
                var error:string = err.error;
                this.showSnackBar(error.substring(error.indexOf(":") + 1));
            });
          }
      }, err=>{
          this.showSnackBar("Something went wrong!!");
      });
    }
  }

  private validateRequest(): boolean{
    if(!this.doctorComment){
      this.showSnackBar("Doctor's comments cannot be empty");
      return false;
    } else if(this.revisit){
      if(!this.revisitDate){
        this.showSnackBar("Please select a revisit date.");
        return false;
      }
      else {
        var doctor: UserDTO = JSON.parse(sessionStorage.getItem(session_user_key));
        var date = this.getDateAsString(this.revisitDate);
        var times:TimeDTO[] = doctor.availableTimes[date];
        if(times){
          var time: TimeDTO = times.find(time=>!time.appointmentId);
          if(!time){
            this.showSnackBar("Doctor is not having any available time on the revisit date.");
            return false;
          } else{
            return true;
          }
        } else {
          this.showSnackBar("Doctor is not available on the revisit date.");
          return false;
        }
      }
    } else {
      return true;
    }
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

  private showSnackBar(msg1: string, msg2?: string){
    this.snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }
}
