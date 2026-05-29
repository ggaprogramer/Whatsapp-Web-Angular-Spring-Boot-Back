package whatsapp.web.friendship.enums;

import lombok.Getter;

@Getter
public enum SituationFriendShip {
    APPROVED("APPROVED"),
    PENDING("PENDING"),
    REJECTED("REJECTED");

    private String description;

    SituationFriendShip(String description){
        this.description = description;
    }
}
