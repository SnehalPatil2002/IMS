package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderRecievedDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderRecievedDTO.class);
        OrderRecievedDTO orderRecievedDTO1 = new OrderRecievedDTO();
        orderRecievedDTO1.setId(1L);
        OrderRecievedDTO orderRecievedDTO2 = new OrderRecievedDTO();
        assertThat(orderRecievedDTO1).isNotEqualTo(orderRecievedDTO2);
        orderRecievedDTO2.setId(orderRecievedDTO1.getId());
        assertThat(orderRecievedDTO1).isEqualTo(orderRecievedDTO2);
        orderRecievedDTO2.setId(2L);
        assertThat(orderRecievedDTO1).isNotEqualTo(orderRecievedDTO2);
        orderRecievedDTO1.setId(null);
        assertThat(orderRecievedDTO1).isNotEqualTo(orderRecievedDTO2);
    }
}
