import { Component, OnInit } from '@angular/core';
import { MatDialogRef} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { LoginDTO } from '../models/LoginDTO';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../user.service';
import { session_user_key, session_isUserLoggedIn, session_user_type } from '../Constants';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public loginDTO:LoginDTO;
  public userType:string = "patient";

  constructor(private dialogRef: MatDialogRef<LoginComponent>, private router: Router, private userService:UserService,
    private snackBar: MatSnackBar) {
      dialogRef.disableClose = true;
     }

  ngOnInit(): void {
    this.loginDTO = new LoginDTO();
  }

  onCancel():void{
    this.dialogRef.close();
  }

  onSignIn():void {
    if(this.validateInputs()){
      this.userService.validateUser(this.loginDTO, this.userType).subscribe(user=>{
        if(user.emailId != undefined){
          sessionStorage.setItem(session_user_key, JSON.stringify(user));
          sessionStorage.setItem(session_isUserLoggedIn, "true");
          sessionStorage.setItem(session_user_type, this.userType);
          this.showSnackBar("Login Success");
          this.dialogRef.close();
          window.location.href = "/";
        } else {
          /*
            user.firtName hold the error message from the server
          */
          this.showSnackBar("Login Failed!! " + user.firstName);
        }

      }, err=>{
        this.showSnackBar(err.error);
      })
    }
    
  }

  private showSnackBar(msg1: string, msg2?: string){
    this.snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  private validateInputs(): Boolean{
    var success:Boolean = true;
    if(this.loginDTO.emailId == undefined){
      this.showSnackBar("Email cannot be empty");
      success = false;
    }
    else if(this.loginDTO.password == undefined){
      this.showSnackBar("Password cannot be empty");
      success = false;
    }

    return success;
  }

}
