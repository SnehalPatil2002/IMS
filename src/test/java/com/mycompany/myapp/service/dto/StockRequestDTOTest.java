package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockRequestDTO.class);
        StockRequestDTO stockRequestDTO1 = new StockRequestDTO();
        stockRequestDTO1.setId(1L);
        StockRequestDTO stockRequestDTO2 = new StockRequestDTO();
        assertThat(stockRequestDTO1).isNotEqualTo(stockRequestDTO2);
        stockRequestDTO2.setId(stockRequestDTO1.getId());
        assertThat(stockRequestDTO1).isEqualTo(stockRequestDTO2);
        stockRequestDTO2.setId(2L);
        assertThat(stockRequestDTO1).isNotEqualTo(stockRequestDTO2);
        stockRequestDTO1.setId(null);
        assertThat(stockRequestDTO1).isNotEqualTo(stockRequestDTO2);
    }
}
