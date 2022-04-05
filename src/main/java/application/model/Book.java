package application.model;

public class Book {
    private String valdeResa;
    private User user;
    public Book(String valdeResa, User user){
        this.valdeResa = valdeResa;
        this.user = user;
    }



    public String getValdeResa() {
        return valdeResa;
    }
}
