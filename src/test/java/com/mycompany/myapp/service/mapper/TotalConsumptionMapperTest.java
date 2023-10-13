package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TotalConsumptionMapperTest {

    private TotalConsumptionMapper totalConsumptionMapper;

    @BeforeEach
    public void setUp() {
        totalConsumptionMapper = new TotalConsumptionMapperImpl();
    }
}
