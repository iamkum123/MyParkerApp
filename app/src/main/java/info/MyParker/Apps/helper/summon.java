package info.MyParker.Apps.helper;

public class summon {
    private String id;
    private  String location;
    private  String date;
    private  String charges;
    private  String des;
    private  String status;
    private String vehicle;


    public summon(String i,String l, String d, String c, String de, String s,String v){

        this.id=i;
        this.location=l;
        this.date=d;
        this.charges=c;
        this.des=de;
        this.status=s;
        this.vehicle=v;
    }
    public  String getID(){
        return id;
    }
    public  String getLocation(){
        return location;
    }
    public  String getDate(){
        return date;
    }
    public  String getPrice(){
        return charges;
    }
    public String getDesc(){
        return des;
    }
    public String getStatus(){
        return status;
    }
    public String getVehicle(){return vehicle;}


}
