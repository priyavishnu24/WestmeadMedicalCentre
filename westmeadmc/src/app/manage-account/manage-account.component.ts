import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { UserDTO } from '../models/UserDTO';
import { session_user_key, session_user_type } from '../Constants';
import { UserService } from '../user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-manage-account',
  templateUrl: './manage-account.component.html',
  styleUrls: ['./manage-account.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ManageAccountComponent implements OnInit {

  user:UserDTO;
  oldPassword:string;
  newPassword:string;
  confirmNewPassword: string;
  userType:string;
  file:File;

  constructor(private userService:UserService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem(session_user_key));
    this.userType = sessionStorage.getItem(session_user_type);
  }

  onUpdate(){
    console.log(this.file);
    if(this.validate()){
      if(this.newPassword){
        this.user.password = this.newPassword;
      }
      if(this.userType == "doctor"){
        this.userService.updateDoctor(this.file, this.user).subscribe(resp=>{
          this.showSnackBar("Updated Successfully");
        }, err=>{
          this.showSnackBar("Updation failed.");
        });
      } else {
        this.userService.updateUser(this.user, this.userType).subscribe(resp=>{
          this.showSnackBar("Updated Successfully");
        }, err=>{
          this.showSnackBar("Updation failed.");
        });
      }
    }
    
    this.oldPassword = "";
    this.newPassword = "";
    this.confirmNewPassword = "";
  }

  onFileSelected(event) {
    this.file = event.target.files[0];
    console.log(this.file);
  }

  private validate(){
    if(this.oldPassword && this.user.password != this.oldPassword){
      this.showSnackBar("Old password mimatch");
      return false;
    } else if(this.oldPassword && this.newPassword != this.confirmNewPassword){
      this.showSnackBar("Password mismatch");
      return false;
    } else if(this.user.firstName == undefined){
      this.showSnackBar("First name cannot be empty");
      return false;
    }
    else if(this.user.lastName == undefined){
      this.showSnackBar("Last name cannot be empty");
      return false;
    }
    else if( this.userType != "admin" && this.user.age == undefined){
      this.showSnackBar("Age cannot be empty");
      return false;
    } else if( this.userType != "admin" && this.user.gender == undefined){
      this.showSnackBar("Gender cannot be empty");
      return false;
    } else if(this.user.phoneNo == undefined){
      this.showSnackBar("Phone No cannot be empty");
      return false;
    } else if(this.userType == "doctor" && this.user.qualification == undefined){
      this.showSnackBar("Qualification cannot be empty");
      return false;
    } else if(this.userType == "doctor" && this.user.experience < 0){
      this.showSnackBar("Experience cannot be negative");
      return false;
    } else if(this.userType == "doctor" && !this.file){
      this.showSnackBar("Choose a photo to upload");
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

}
