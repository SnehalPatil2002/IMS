package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesOrderdMapperTest {

    private SalesOrderdMapper salesOrderdMapper;

    @BeforeEach
    public void setUp() {
        salesOrderdMapper = new SalesOrderdMapperImpl();
    }
}
