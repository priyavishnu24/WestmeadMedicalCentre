import { Component, OnInit, ViewEncapsulation, ViewChildren, QueryList } from '@angular/core';
import { UserDTO, Approval } from '../models/UserDTO';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../user.service';
import { session_user_key } from '../Constants';

@Component({
  selector: 'app-admin-approval',
  templateUrl: './admin-approval.component.html',
  styleUrls: ['./admin-approval.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AdminApprovalComponent implements OnInit {

  currentUser:UserDTO;
  doctors:MatTableDataSource<UserDTO>;
  admins:MatTableDataSource<UserDTO>;
  doctorColumns: string[] = ['firstName', 'lastName', 'age', 'gender', 'emailId', 'phoneNo', 'qualification', 'experience', 'action'];
  adminColumns: string[] = ['firstName', 'lastName', 'emailId', 'phoneNo', 'action'];
  @ViewChildren(MatPaginator) paginators: QueryList<MatPaginator>;
  
  constructor(private userService:UserService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.userService.getNonApprovedUsers("admin").subscribe(response=>{
      this.admins = new MatTableDataSource<UserDTO>(response);
      this.admins.paginator = this.paginators.last;
    })

    this.userService.getNonApprovedUsers("doctor").subscribe(response=>{
      this.doctors = new MatTableDataSource<UserDTO>(response);
      this.doctors.paginator = this.paginators.first;
    });

    this.currentUser = JSON.parse(sessionStorage.getItem(session_user_key));

  }

  ngAfterViewInit() {
  }

  onApprove(user:UserDTO, userType:string):void{
    console.log(user);
    console.log(userType);
    user.approval = new Approval();
    user.approval.status = "approved";
    user.approval.approvedBy = this.currentUser.emailId;
    user.approval.approvedDate = new Date();

    this.userService.updateUserApproval(user, userType).subscribe(resp=>{
      if(resp){
        this.showSnackBar(user.lastName + " approved");
        this.removeApprovedOrRejectedUser(user, userType);
      }
    }, err=>{
      this.showSnackBar(user.lastName + " approval failed");
    })
  }

  onReject(user:UserDTO, userType:string):void{
    console.log(user);
    console.log(userType);
    user.approval = new Approval();
    user.approval.status = "rejected";
    user.approval.approvedBy = this.currentUser.emailId;
    user.approval.approvedDate = new Date();

    this.userService.updateUserApproval(user, userType).subscribe(resp=>{
      if(resp){
        this.showSnackBar(user.lastName + " rejected");
        this.removeApprovedOrRejectedUser(user, userType);
      }
    }, err=>{
      this.showSnackBar(user.lastName + " approval failed");
    })
  }

  private showSnackBar(msg1: string, msg2?: string){
    this.snackBar.open(msg1, msg2, {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  private removeApprovedOrRejectedUser(user:UserDTO, userType:string){
    if(userType == 'admin'){
      this.admins = new MatTableDataSource<UserDTO>(this.admins.data.filter(userObj=>user.emailId != userObj.emailId));
    } else if (userType == 'doctor'){
      this.doctors = new MatTableDataSource<UserDTO>(this.doctors.data.filter(userObj=>user.emailId != userObj.emailId));
    }
  }

}
