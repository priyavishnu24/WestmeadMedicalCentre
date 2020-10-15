import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import { LoginComponent } from '../login/login.component';
import { RegistrationComponent } from '../registration/registration.component';
import { LoginDTO } from '../models/LoginDTO';
import { session_isUserLoggedIn, session_user_key, session_user_type } from '../Constants';
import { UserDTO } from '../models/UserDTO';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  public user:UserDTO;
  public userType: string;
  public isUserLoggedIn:boolean;

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem(session_user_key));
    this.isUserLoggedIn = JSON.parse(sessionStorage.getItem(session_isUserLoggedIn));
    this.userType = sessionStorage.getItem(session_user_type);
    console.log(this.userType);
  }

  openLoginDialog():void{
    const dialogRef = this.dialog.open(LoginComponent, {
      width: '300px'
    });

    
  }

  openRegistrationDialog(userType:string): void {
    const dialogRef = this.dialog.open(RegistrationComponent, {
      width: '600px',
      data: userType
    });

  }

  onSignOut():void {
    sessionStorage.removeItem(session_user_key);
    sessionStorage.removeItem(session_isUserLoggedIn)
    window.location.href = "/";
  }

}
