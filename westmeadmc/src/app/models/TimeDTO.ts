export class TimeDTO {
    public time:string = "";
    public isSelected:boolean = false;
    public isAlreadySelected:boolean = false;
    public isUnSelected:boolean = false; // true only when alreadySelected is unselected
    public appointmentId:string;
    public toJSON = function() {
        return {
          time: this.time,
          appointmentId : this.appointmentId
        };
      };
}