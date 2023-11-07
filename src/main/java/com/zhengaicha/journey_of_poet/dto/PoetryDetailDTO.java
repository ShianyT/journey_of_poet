package com.zhengaicha.journey_of_poet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PoetryDetailDTO {
    private String poem;
    private Integer poemId;
    private int count;
}
