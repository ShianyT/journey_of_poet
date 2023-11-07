package com.zhengaicha.journey_of_poet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BattleUserDTO {
    private UserDTO battleUser;
    private boolean is_before;
    private String keyword;
}
