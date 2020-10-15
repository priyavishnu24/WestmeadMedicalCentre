import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserDTO } from '../models/UserDTO';
import { UserService } from '../user.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  public confirmPassword: string;
  public user: UserDTO;

  constructor(private dialogRef: MatDialogRef<RegistrationComponent>, private userService:UserService, private _snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public userType: string) {
      
     }

  ngOnInit(): void {
    this.user = new UserDTO();
  }

  onCancel():void{
    this.dialogRef.close();
  }

  onRegister():void {
    console.log(JSON.stringify(this.user));
    if(this.validateInputs()){
      this.userService.registerUser(this.user, this.userType).subscribe(response=>{
        console.log(response);
        if(response == "Success"){
          this.showSnackBar("Registration successful!", "Login now");
        } else if(response == "E-mail id already registered") {
          this.showSnackBar("Registration failed!", response);
        } else {
          this.showSnackBar("Registration failed!");
        }
      }, err=>{
        this.showSnackBar(err.error);
      });
    
      this.dialogRef.close();
    }
  }

  private showSnackBar(msg1: string, msg2?: string){
    this._snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  private validateInputs():Boolean{
    var success:Boolean = true;
    if(this.user.firstName == undefined){
      this.showSnackBar("First name cannot be empty");
      success = false;
    }
    else if(this.user.lastName == undefined){
      this.showSnackBar("Last name cannot be empty");
      success = false;
    }
    else if(this.user.emailId == undefined){
      this.showSnackBar("E-mail id cannot be empty");
      success = false;
    }
    else if(this.user.phoneNo == undefined){
      this.showSnackBar("Phone No cannot be empty");
      success = false;
    }
    else if(this.user.password == undefined){
      this.showSnackBar("Password cannot be empty");
      success = false;
    }
    else if(this.confirmPassword == undefined){
      this.showSnackBar("Confirm password cannot be empty");
      success = false;
    }
    else if(this.user.password != this.confirmPassword){
      this.showSnackBar("Password mismatch");
      success = false;
    }

    return success;
  }

}
