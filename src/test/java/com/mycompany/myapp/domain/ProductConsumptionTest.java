package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductConsumptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductConsumption.class);
        ProductConsumption productConsumption1 = new ProductConsumption();
        productConsumption1.setId(1L);
        ProductConsumption productConsumption2 = new ProductConsumption();
        productConsumption2.setId(productConsumption1.getId());
        assertThat(productConsumption1).isEqualTo(productConsumption2);
        productConsumption2.setId(2L);
        assertThat(productConsumption1).isNotEqualTo(productConsumption2);
        productConsumption1.setId(null);
        assertThat(productConsumption1).isNotEqualTo(productConsumption2);
    }
}
