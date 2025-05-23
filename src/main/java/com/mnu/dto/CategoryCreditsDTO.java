package com.mnu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryCreditsDTO {
    private String category;
    private int totalCredits;
}
