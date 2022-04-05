package application.model;

public class User {
    private String name, lname, adress, email, number, password, id;
    private boolean isAdmin;

    // Constructor
    public User(String id,String name, String lname, String adress, String email, String number, String password, boolean isAdmin){
        this.id = id;
        this.name = name;
        this.lname = lname;
        this.adress = adress;
        this.email = email;
        this.number = number;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getter and setter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLname() {
        return lname;
    }

    public String getAdress() {
        return adress;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // info
    public String getToString(){
        return "info: " + "name: "+name + " \n" +
                  "last name: " +lname + " \n" +
                  "address: "+adress + " \n" +
                  "email: "+email + " \n"+
                  "number: "+number ;
    }
}

