
package worldMap;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class CountryRegion implements CRegion {
    private String        name;
    private List<Country> countries;


    // ******************** Constructors **************************************
    public CountryRegion(final String NAME, final Country... COUNTRIES) {
        name      = NAME;
        countries = new ArrayList<>(COUNTRIES.length);
        for (Country country : COUNTRIES) { countries.add(country); }
    }


    // ******************** Methods *******************************************
    @Override public String name() { return name; }

    @Override public List<Country> getCountries() { return countries; }

    @Override public void setColor(final Color COLOR) {
        for (Country country : getCountries()) { country.setColor(COLOR); }
    }
}
