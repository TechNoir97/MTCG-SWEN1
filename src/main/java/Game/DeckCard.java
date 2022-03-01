package Game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

public class DeckCard {

    @Getter
    private String Id;
    @Getter
    private String Name;

    @Getter
    private Integer Damage;
    @Getter
    private String element;
    @Getter
    private String type;


    public DeckCard(String id, String name, Integer damage, String element, String type) {
        this.Id=id;
        this.Name=name;
        this.Damage=damage;
        this.element=element;
        this.type=type;
    }


}
