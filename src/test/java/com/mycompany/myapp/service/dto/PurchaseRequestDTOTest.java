package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseRequestDTO.class);
        PurchaseRequestDTO purchaseRequestDTO1 = new PurchaseRequestDTO();
        purchaseRequestDTO1.setId(1L);
        PurchaseRequestDTO purchaseRequestDTO2 = new PurchaseRequestDTO();
        assertThat(purchaseRequestDTO1).isNotEqualTo(purchaseRequestDTO2);
        purchaseRequestDTO2.setId(purchaseRequestDTO1.getId());
        assertThat(purchaseRequestDTO1).isEqualTo(purchaseRequestDTO2);
        purchaseRequestDTO2.setId(2L);
        assertThat(purchaseRequestDTO1).isNotEqualTo(purchaseRequestDTO2);
        purchaseRequestDTO1.setId(null);
        assertThat(purchaseRequestDTO1).isNotEqualTo(purchaseRequestDTO2);
    }
}
