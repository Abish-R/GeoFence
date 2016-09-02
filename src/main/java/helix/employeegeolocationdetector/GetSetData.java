package helix.employeegeolocationdetector;

/**
 * Created by HelixTech-Admin on 3/12/2016.
 */
public class GetSetData {
    int id;
    String dr_sno,dr_id,dr_name,dr_place,dr_lat,dr_lon,dr_radius,dr_crtd_dt,rep_sno,rep_id,rep_time_of_visit,
            rep_visited_dr_id,rep_lat,rep_lon,rep_location,rep_up_status;
    public GetSetData(){}

    public GetSetData(String d_id, String d_nm, String d_plc, String d_lat, String d_lon,
                      String d_rad){//} String cr_dt){
        //this.dr_sno = sno;
        this.dr_id = d_id;
        this.dr_name = d_nm;
        this.dr_place=d_plc;
        this.dr_lat=d_lat;
        this.dr_lon=d_lon;
        this.dr_radius=d_rad;
        //this.dr_crtd_dt=cr_dt;
    }
    public GetSetData(String sno, String r_id, String r_tm_vst, String r_v_dr_id,
                      String r_lat, String r_lon, String r_loc, String r_up_st){
        this.rep_sno=sno;
        this.rep_id=r_id;
        this.rep_time_of_visit=r_tm_vst;
        this.rep_visited_dr_id=r_v_dr_id;
        this.rep_lat=r_lat;
        this.rep_lon=r_lon;
        this.rep_location=r_loc;
        this.rep_up_status=r_up_st;
    }

    /** getting ID*/
    public int getID(){
        return this.id;
    }
    public String getDrId(){
        return this.dr_id;
    }
    public String getDrName(){
        return this.dr_name;
    }
    public String getDrPlace(){
        return this.dr_place;
    }
    public String getDrLat(){
        return this.dr_lat;
    }
    public String getDrLon(){
        return this.dr_lon;
    }
    public String getDrRadius(){
        return this.dr_radius;
    }
    public String getDrCreatedDate(){
        return this.dr_crtd_dt;
    }
    public String getRepSNo(){
        return this.rep_sno;
    }
    public String getRepId(){
        return this.rep_id;
    }
    public String getRepVisitedTime(){
        return this.rep_time_of_visit;
    }
    public String getRepVisitedDrId(){
        return this.rep_visited_dr_id;
    }
    public String getRepLat(){
        return this.rep_lat;
    }
    public String getRepLon(){
        return this.rep_lon;
    }
    public String getRepLocation(){
        return this.rep_location;
    }
    public String getRepUpState(){
        return this.rep_up_status;
    }


    public void setID(int id){
        this.id = id;
    }
    public void setDrId(String d_id){
        this.dr_id = d_id;
    }
    public void setDrName(String d_nm){
        this.dr_name = d_nm;
    }
    public void setDrPlace(String d_plc){
        this.dr_place = d_plc;
    }
    public void setDrLat(String d_lat){
        this.dr_lat = d_lat;
    }
    public void setDrLon(String d_lon){
        this.dr_lon = d_lon;
    }
    public void setDrRadius(String d_rad){
        this.dr_radius = d_rad;
    }
    public void setDrCreatedDate(String cr_dt){
        this.dr_crtd_dt = cr_dt;
    }
    public void setRepSNo(String sno){
        this.rep_sno = sno;
    }
    public void setRepId(String r_id){
        this.rep_id = r_id;
    }
    public void setRepVisitedTime(String r_tm_vst){
        this.rep_time_of_visit = r_tm_vst;
    }
    public void setRepVisitedDrId(String r_v_dr_id){
        this.rep_visited_dr_id = r_v_dr_id;
    }
    public void setRepLat(String r_lat){
        this.rep_lat = r_lat;
    }
    public void setRepLon(String lon){
        this.rep_lon = lon;
    }
    public void setRepLocation(String r_loc){
        this.rep_location = r_loc;
    }
    public void setRepUpState(String r_up_st){
        this.rep_up_status = r_up_st;
    }
}
