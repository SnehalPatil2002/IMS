package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TotalConsumptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TotalConsumption.class);
        TotalConsumption totalConsumption1 = new TotalConsumption();
        totalConsumption1.setId(1L);
        TotalConsumption totalConsumption2 = new TotalConsumption();
        totalConsumption2.setId(totalConsumption1.getId());
        assertThat(totalConsumption1).isEqualTo(totalConsumption2);
        totalConsumption2.setId(2L);
        assertThat(totalConsumption1).isNotEqualTo(totalConsumption2);
        totalConsumption1.setId(null);
        assertThat(totalConsumption1).isNotEqualTo(totalConsumption2);
    }
}
