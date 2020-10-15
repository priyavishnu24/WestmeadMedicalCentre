import { TreatmentDTO } from './TreatmentDTO';
import { UserDTO } from './UserDTO';

export class AppointmentDTO {
    appointmentId: string;
    appointmentTime: string;
    patient :  UserDTO =  new UserDTO();
    doctor : UserDTO = new UserDTO();
    reason: string;
    bookedBy: string;
    treatment: TreatmentDTO;
}