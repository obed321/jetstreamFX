
package worldMap;

import javafx.scene.paint.Color;

import java.util.List;

public interface CRegion {

    String name();

    List<Country> getCountries();

    void setColor(final Color COLOR);
}
