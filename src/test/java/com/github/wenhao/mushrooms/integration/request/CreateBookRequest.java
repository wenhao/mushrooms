package com.github.wenhao.mushrooms.integration.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBookRequest {
    
    private String name;
    private BigDecimal price;
}
