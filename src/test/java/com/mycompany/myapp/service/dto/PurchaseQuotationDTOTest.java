package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseQuotationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseQuotationDTO.class);
        PurchaseQuotationDTO purchaseQuotationDTO1 = new PurchaseQuotationDTO();
        purchaseQuotationDTO1.setId(1L);
        PurchaseQuotationDTO purchaseQuotationDTO2 = new PurchaseQuotationDTO();
        assertThat(purchaseQuotationDTO1).isNotEqualTo(purchaseQuotationDTO2);
        purchaseQuotationDTO2.setId(purchaseQuotationDTO1.getId());
        assertThat(purchaseQuotationDTO1).isEqualTo(purchaseQuotationDTO2);
        purchaseQuotationDTO2.setId(2L);
        assertThat(purchaseQuotationDTO1).isNotEqualTo(purchaseQuotationDTO2);
        purchaseQuotationDTO1.setId(null);
        assertThat(purchaseQuotationDTO1).isNotEqualTo(purchaseQuotationDTO2);
    }
}
