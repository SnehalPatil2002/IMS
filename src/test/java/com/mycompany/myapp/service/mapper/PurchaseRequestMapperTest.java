package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseRequestMapperTest {

    private PurchaseRequestMapper purchaseRequestMapper;

    @BeforeEach
    public void setUp() {
        purchaseRequestMapper = new PurchaseRequestMapperImpl();
    }
}
