package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesOrderdDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesOrderdDTO.class);
        SalesOrderdDTO salesOrderdDTO1 = new SalesOrderdDTO();
        salesOrderdDTO1.setId(1L);
        SalesOrderdDTO salesOrderdDTO2 = new SalesOrderdDTO();
        assertThat(salesOrderdDTO1).isNotEqualTo(salesOrderdDTO2);
        salesOrderdDTO2.setId(salesOrderdDTO1.getId());
        assertThat(salesOrderdDTO1).isEqualTo(salesOrderdDTO2);
        salesOrderdDTO2.setId(2L);
        assertThat(salesOrderdDTO1).isNotEqualTo(salesOrderdDTO2);
        salesOrderdDTO1.setId(null);
        assertThat(salesOrderdDTO1).isNotEqualTo(salesOrderdDTO2);
    }
}
