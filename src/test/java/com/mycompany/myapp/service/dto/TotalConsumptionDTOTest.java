package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TotalConsumptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TotalConsumptionDTO.class);
        TotalConsumptionDTO totalConsumptionDTO1 = new TotalConsumptionDTO();
        totalConsumptionDTO1.setId(1L);
        TotalConsumptionDTO totalConsumptionDTO2 = new TotalConsumptionDTO();
        assertThat(totalConsumptionDTO1).isNotEqualTo(totalConsumptionDTO2);
        totalConsumptionDTO2.setId(totalConsumptionDTO1.getId());
        assertThat(totalConsumptionDTO1).isEqualTo(totalConsumptionDTO2);
        totalConsumptionDTO2.setId(2L);
        assertThat(totalConsumptionDTO1).isNotEqualTo(totalConsumptionDTO2);
        totalConsumptionDTO1.setId(null);
        assertThat(totalConsumptionDTO1).isNotEqualTo(totalConsumptionDTO2);
    }
}
