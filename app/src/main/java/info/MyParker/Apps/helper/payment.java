package info.MyParker.Apps.helper;

public class payment {
    private  String location;
    private  String price;
    private  String start_time;
    private  String end_time;
    private String vehicle;
    private String status;


    public payment(){
        this.location="";
        this.price="";
        this.start_time="";
        this.end_time="";
        this.vehicle="";
    }
    public payment(String l, String p, String s, String e,String v,String stat){
        this.location=l;
        this.price=p;
        this.start_time=s;
        this.end_time=e;
        this.vehicle=v;
        this.status=stat;
    }

    public  String getLocation(){
        return location;
    }
    public String getPrice(){
        return price;
    }
    public String getStart(){
        return start_time;
    }
    public String getEnd(){
        return end_time;
    }
    public String getVehicle(){
        return vehicle;
    }
    public String getStatus(){
        return status;
    }


}
