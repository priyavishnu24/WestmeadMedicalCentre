import { TimeDTO } from './TimeDTO';

export class UserDTO {
    firstName: string;
    lastName: string;
    age: number; //Only for Doctor and Patient
    gender: string = "male"; //Only for Doctor and Patient
    emailId: string;
    phoneNo: number;
    password: string;
    qualification: string; //Only for Doctor
    experience: number; //Only for Doctor
    imageURL : string; // Only for Doctor 
    highlighted?:boolean = false; // Only for Doctor
    approval : Approval; // Only for Doctor and Admin
    availableTimes: Map<string, TimeDTO[]>;
  }

  export class Approval {
    status: string;
    approvedBy: string;
    approvedDate: Date;
  }