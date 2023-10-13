package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RawMaterialConsumptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RawMaterialConsumptionDTO.class);
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO1 = new RawMaterialConsumptionDTO();
        rawMaterialConsumptionDTO1.setId(1L);
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO2 = new RawMaterialConsumptionDTO();
        assertThat(rawMaterialConsumptionDTO1).isNotEqualTo(rawMaterialConsumptionDTO2);
        rawMaterialConsumptionDTO2.setId(rawMaterialConsumptionDTO1.getId());
        assertThat(rawMaterialConsumptionDTO1).isEqualTo(rawMaterialConsumptionDTO2);
        rawMaterialConsumptionDTO2.setId(2L);
        assertThat(rawMaterialConsumptionDTO1).isNotEqualTo(rawMaterialConsumptionDTO2);
        rawMaterialConsumptionDTO1.setId(null);
        assertThat(rawMaterialConsumptionDTO1).isNotEqualTo(rawMaterialConsumptionDTO2);
    }
}
