import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDTO } from './models/UserDTO';
import { 
  register_service_url,
  login_service_url,
  get_non_approved_user_url,
  approve_user_url,
  get_approved_doctor_url,
  book_appointment_url,
  add_doctor_availability_url,
  remove_doctor_availability_url,
  get_doctor_url,
  update_doctor_url,
  update_user_url,
  get_appointments_url,
  cancel_appointment_url,
  save_treatment_url
} from './Constants';
import { LoginDTO } from './models/LoginDTO';
import { AppointmentDTO } from './models/AppointmentDTO';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  public registerUser(user:UserDTO, userType:string):Observable<any>{
    return this.http.post<string>(register_service_url.replace("<USER_TYPE>", userType), user, {
      responseType : 'text' as 'json'
    });
  }

  public validateUser(loginObj: LoginDTO, userType:string):Observable<UserDTO>{
    return this.http.post<UserDTO>(login_service_url.replace("<USER_TYPE>", userType) + "?emailId=" + loginObj.emailId + "&password=" + loginObj.password, "");
  }

  public getNonApprovedUsers(userType:string):Observable<UserDTO[]>{
    return this.http.get<UserDTO[]>(get_non_approved_user_url.replace("<USER_TYPE>", userType));
  }

  public updateUserApproval(user:UserDTO,userType:string):Observable<Boolean> {
    return this.http.post<Boolean>(approve_user_url.replace("<USER_TYPE>", userType), user, {
      responseType : 'text' as 'json'
    });
  }

  public getApprovedDoctors():Observable<UserDTO[]>{
    return this.http.get<UserDTO[]>(get_approved_doctor_url);
  }

  public bookAppointment(patientId:string, doctorId:string, date:string, time: string, reason:string, userType:string, adminId:string):Observable<boolean>{
    var url = book_appointment_url.replace("<USER_TYPE>", userType) + "?";
    url = url + "patientId=" + patientId;
    url = url + "&doctorId=" + doctorId;
    url = url + "&date=" + date;
    url = url + "&time=" + time;
    url = url + "&reason=" + reason;
    url = url + "&adminId=" + adminId;
    return this.http.post<boolean>(url,"");
  }

  public addDoctorAvailability(doctorId:string, date:string, times:string[]):Observable<UserDTO>{
    var url = add_doctor_availability_url + "?";
    url = url + "doctorId=" + doctorId;
    url = url + "&date=" + date;
    url = url + "&times=" + times;
    return this.http.post<UserDTO>(url, "");
  }

  public removeDoctorAvailability(doctorId:string, date:string, times:string[]):Observable<UserDTO>{
    var url = remove_doctor_availability_url + "?";
    url = url + "doctorId=" + doctorId;
    url = url + "&date=" + date;
    url = url + "&times=" + times;
    return this.http.post<UserDTO>(url, "");
  }

  public getDoctor(doctorId:string):Observable<UserDTO>{
    return this.http.get<UserDTO>(get_doctor_url+"?doctorId="+doctorId);
  }

  public updateDoctor(file:File, doctor:UserDTO):Observable<void>{
    const formData = new FormData();
    formData.append('doctor', JSON.stringify(doctor));
    formData.append('file', file); 
    return this.http.post<void>(update_doctor_url, formData);
  }
  
  public updateUser(user: UserDTO, userType: string):Observable<void>{
    return this.http.post<void>(update_user_url.replace("<USER_TYPE>", userType), user);
  }

  public getAppointments(id: string, userType:string, date?: string):Observable<AppointmentDTO[]>{
    var url = get_appointments_url.replace("<USER_TYPE>", userType) + "?";
    if(userType == "doctor")
      url = url + "doctorId=" + id;
    else
      url = url + "patientId=" + id;
    url = url + "&date=" + date;
    return this.http.get<AppointmentDTO[]>(url);
  }

  public cancelAppointment(appointmentId: string):Observable<boolean>{
    return this.http.get<boolean>(cancel_appointment_url + "?appointmentId=" + appointmentId);
  }

  public saveTreatment(files: File[], comments: string, appointmentId: string):Observable<void>{
    const formData = new FormData();
    formData.append('comments', comments);
    if(files)
    for(var i=0; i<files.length; i++)
      formData.append('files', files[i]);
    formData.append('appointmentId', appointmentId);
    return this.http.post<void>(save_treatment_url, formData);
  }
}
