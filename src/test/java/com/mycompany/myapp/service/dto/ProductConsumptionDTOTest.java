package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductConsumptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductConsumptionDTO.class);
        ProductConsumptionDTO productConsumptionDTO1 = new ProductConsumptionDTO();
        productConsumptionDTO1.setId(1L);
        ProductConsumptionDTO productConsumptionDTO2 = new ProductConsumptionDTO();
        assertThat(productConsumptionDTO1).isNotEqualTo(productConsumptionDTO2);
        productConsumptionDTO2.setId(productConsumptionDTO1.getId());
        assertThat(productConsumptionDTO1).isEqualTo(productConsumptionDTO2);
        productConsumptionDTO2.setId(2L);
        assertThat(productConsumptionDTO1).isNotEqualTo(productConsumptionDTO2);
        productConsumptionDTO1.setId(null);
        assertThat(productConsumptionDTO1).isNotEqualTo(productConsumptionDTO2);
    }
}
