package whatsapp.web.profile.enums;

import lombok.Getter;

@Getter
public enum UnitSizeFile {
    B("Bytes"),
    KB("KB"),
    MB("MB"),
    GB("GB"),
    TB("TB");

    private String description;

    UnitSizeFile(String description){
        this.description = description;
    }
}
