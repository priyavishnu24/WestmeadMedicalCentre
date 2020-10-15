import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { TimeDTO } from '../models/TimeDTO';
import { session_user_key, session_user_type } from '../Constants';
import { UserDTO } from '../models/UserDTO';
import { UserService } from '../user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-doctor-availability',
  templateUrl: './doctor-availability.component.html',
  styleUrls: ['./doctor-availability.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DoctorAvailabilityComponent implements OnInit {

  availableDate: Date;
  shiftType: string;
  times:TimeDTO[] = [];
  private user:UserDTO;
  doctorId:string;
  isAdmin:boolean;

  constructor(private userService: UserService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.availableDate = new Date();
    this.shiftType = "shift1";
    this.isAdmin = (sessionStorage.getItem(session_user_type) == "admin");
    console.log(this.isAdmin);
    if(!this.isAdmin){
      this.user = JSON.parse(sessionStorage.getItem(session_user_key));
      this.doctorId = this.user.emailId;
     
      this.onShiftChange();
    
      //this.isTimeAlreadySelected();
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

  onShiftChange(){
    this.times = [];
    var startTime = 8;
    var endTime = 16
    var timeStr = "";

    if(this.shiftType == "shift2"){
      startTime = 16;
      endTime = 24;
    } else if(this.shiftType == "shift3"){
      startTime = 0;
      endTime = 8;      
    }
    for(var i=startTime; i<endTime; i++){
      timeStr = i<10? "0" + i + ":" : i + ":";
      for (var min=0; min<60; min=min+20){
        var minStr = min<10? "0"+min+":" : min+":";
        var finalTimeStr = timeStr + minStr + "00";
        var time:TimeDTO = new TimeDTO();
        time.time = finalTimeStr;
        this.times.push(time);
      }
      timeStr = "";
    }
    this.isTimeAlreadySelected();
  }
  addAvailability(){
    if(this.validateRequest()){
      var addedTimes: string[] = [];
      var removedTimes : string[] = [];
      this.times.forEach(timeObj=>{
        if(timeObj.isSelected){
          addedTimes.push(timeObj.time);
        }
        if(timeObj.isUnSelected){
          removedTimes.push(timeObj.time);
        }
      });

      if(addedTimes.length > 0){
        //New Times to be added
        this.userService.addDoctorAvailability(this.doctorId, this.getDateAsString(this.availableDate), addedTimes).subscribe(resp=>{
          sessionStorage.setItem(session_user_key, JSON.stringify(resp));
          this.user = resp;   
          this.onShiftChange();
          this.showSnackBar("Successfully Updated");
        }, err=>{
          this.showSnackBar(err.error);
        });
      }

      if(removedTimes.length > 0){
        //Times to be removed
        this.userService.removeDoctorAvailability(this.doctorId, this.getDateAsString(this.availableDate), removedTimes).subscribe(resp=>{
          sessionStorage.setItem(session_user_key, JSON.stringify(resp));
          this.user = resp;   
          this.onShiftChange();
          this.showSnackBar("Successfully Updated");
        }, err=>{
          this.showSnackBar(err.error);
        });
      }
    }
  }

  isTimeAlreadySelected(){
    if(this.user.availableTimes){
        var timesArr: TimeDTO[] = this.user.availableTimes[this.getDateAsString(this.availableDate)];
        if(timesArr){
          timesArr.forEach(timeObj=>{
            this.times.forEach(timeUIObj=>{
              if(timeObj.time == timeUIObj.time){
                timeUIObj.appointmentId = timeObj.appointmentId;
                timeUIObj.isAlreadySelected = true;
              }
            })
          });
        } 
      }
  }

  onChangeDate(){
    this.times = [];
    this.onShiftChange();
    this.isTimeAlreadySelected();
  }

  private validateRequest(){
    if(!this.doctorId){
      this.showSnackBar("Please enter doctor Id");
      return false;
    } if(!this.times.some(time=>time.isSelected) && !this.times.some(time=>time.isUnSelected)){
      this.showSnackBar("Please select/unselect time");
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

  onTimeClick(time:TimeDTO){
    if(time.isAlreadySelected){
      time.isUnSelected = !time.isUnSelected;
    } else {
      time.isSelected = !time.isSelected;
    }
  }

  getColor(time:TimeDTO):string{
    if(time.isUnSelected || (!time.isAlreadySelected && !time.isSelected)){
      return "";
    } else if(!time.isUnSelected && time.isAlreadySelected){
      return "primary";
    } else if(!time.isAlreadySelected && time.isSelected){
      return "accent";
    }
  }

  onBlurDoctorId(){
    if(this.doctorId){
      this.userService.getDoctor(this.doctorId).subscribe(resp=>{
          this.user = resp;   
          this.onShiftChange();
      }, err=>{
          this.showSnackBar(err.error);
      })
    }
  }
}
