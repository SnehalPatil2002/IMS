package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseQuotationDetailsMapperTest {

    private PurchaseQuotationDetailsMapper purchaseQuotationDetailsMapper;

    @BeforeEach
    public void setUp() {
        purchaseQuotationDetailsMapper = new PurchaseQuotationDetailsMapperImpl();
    }
}
