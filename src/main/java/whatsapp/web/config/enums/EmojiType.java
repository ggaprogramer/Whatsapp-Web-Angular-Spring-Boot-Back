package whatsapp.web.config.enums;

import lombok.Getter;

@Getter
public enum EmojiType {
    SMILEYS_AND_PEOPLE("SMILEYS_AND_PEOPLE"),
    ANIMALS_AND_NATURE("ANIMALS_AND_NATURE"),
    FOOD_AND_DRINK("FOOD_AND_DRINK"),
    ACTIVITIES("ACTIVITIES"),
    TRAVEL_AND_PLACES("TRAVEL_AND_PLACES"),
    OBJECTS("OBJECTS"),
    SYMBOLS("SYMBOLS"),
    FLAGS("FLAGS");

    private String descricao;

    EmojiType(String descricao){
        this.descricao = descricao;
    }
}
