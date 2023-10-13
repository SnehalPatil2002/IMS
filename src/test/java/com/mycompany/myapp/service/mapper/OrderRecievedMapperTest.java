package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderRecievedMapperTest {

    private OrderRecievedMapper orderRecievedMapper;

    @BeforeEach
    public void setUp() {
        orderRecievedMapper = new OrderRecievedMapperImpl();
    }
}
