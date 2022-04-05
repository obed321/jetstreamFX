package application.model;

public class Flight {
    private String from, destination, date, time;
    public Flight(String from, String distination, String date, String time){
        this.from = from;
        this.destination = distination;
        this.date = date;
        this.time = time;
    }


    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }
}
