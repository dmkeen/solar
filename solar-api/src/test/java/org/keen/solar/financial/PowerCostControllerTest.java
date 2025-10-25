package org.keen.solar.financial;

import org.junit.jupiter.api.Test;
import org.keen.solar.financial.dal.PowerCostDao;
import org.keen.solar.financial.domain.PowerCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PowerCostController.class)
class PowerCostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PowerCostDao powerCostDao;

    @Test
    void givenPowerCostList_whenCallCostEndpoint_thenTotalCostReturned() throws Exception {
        // Given
        Random random = new Random(1);
        List<PowerCost> data = random.doubles(1000000, 0, 0.00001)
                .boxed()
                .map(value -> new PowerCost(BigDecimal.valueOf(value), 0, 0))
                .toList();

        long fromEpochSeconds = 0;
        long toEpochSeconds = 10;

        when(powerCostDao.getPowerCosts(fromEpochSeconds, toEpochSeconds)).thenReturn(data);

        // When
        mockMvc.perform(get("/cost")
                        .param("fromEpochSeconds", Long.toString(fromEpochSeconds))
                        .param("toEpochSeconds", Long.toString(toEpochSeconds)))

        // Then
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value("4.997917407040086150256254226"));
    }
}
