package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RawMaterialConsumptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RawMaterialConsumption.class);
        RawMaterialConsumption rawMaterialConsumption1 = new RawMaterialConsumption();
        rawMaterialConsumption1.setId(1L);
        RawMaterialConsumption rawMaterialConsumption2 = new RawMaterialConsumption();
        rawMaterialConsumption2.setId(rawMaterialConsumption1.getId());
        assertThat(rawMaterialConsumption1).isEqualTo(rawMaterialConsumption2);
        rawMaterialConsumption2.setId(2L);
        assertThat(rawMaterialConsumption1).isNotEqualTo(rawMaterialConsumption2);
        rawMaterialConsumption1.setId(null);
        assertThat(rawMaterialConsumption1).isNotEqualTo(rawMaterialConsumption2);
    }
}
