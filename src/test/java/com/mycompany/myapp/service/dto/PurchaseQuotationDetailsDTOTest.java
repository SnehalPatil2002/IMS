package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseQuotationDetailsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseQuotationDetailsDTO.class);
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO1 = new PurchaseQuotationDetailsDTO();
        purchaseQuotationDetailsDTO1.setId(1L);
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO2 = new PurchaseQuotationDetailsDTO();
        assertThat(purchaseQuotationDetailsDTO1).isNotEqualTo(purchaseQuotationDetailsDTO2);
        purchaseQuotationDetailsDTO2.setId(purchaseQuotationDetailsDTO1.getId());
        assertThat(purchaseQuotationDetailsDTO1).isEqualTo(purchaseQuotationDetailsDTO2);
        purchaseQuotationDetailsDTO2.setId(2L);
        assertThat(purchaseQuotationDetailsDTO1).isNotEqualTo(purchaseQuotationDetailsDTO2);
        purchaseQuotationDetailsDTO1.setId(null);
        assertThat(purchaseQuotationDetailsDTO1).isNotEqualTo(purchaseQuotationDetailsDTO2);
    }
}
