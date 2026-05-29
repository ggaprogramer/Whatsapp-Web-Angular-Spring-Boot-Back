package whatsapp.web.friendship.dto;

import whatsapp.web.friendship.enums.SituationFriendShip;

public record ProfileFormattedDTO(String name,
                                  String username,
                                  String email,
                                  String description,
                                  String phone,
                                  String linkPhoto,
                                  SituationFriendShip situationFriendship
) {

}
