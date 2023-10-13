package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockRequestMapperTest {

    private StockRequestMapper stockRequestMapper;

    @BeforeEach
    public void setUp() {
        stockRequestMapper = new StockRequestMapperImpl();
    }
}
