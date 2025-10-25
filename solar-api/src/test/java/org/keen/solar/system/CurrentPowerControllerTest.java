package org.keen.solar.system;

import org.junit.jupiter.api.Test;
import org.keen.solar.system.dal.CurrentPowerDao;
import org.keen.solar.system.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrentPowerController.class)
class CurrentPowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrentPowerDao currentPowerDao;

    @Test
    void givenCurrentPowerList_whenCallCurrentPowerSurplusEndpoint_thenSurplusPowerReturned() throws Exception {
        // Given
        List<CurrentPower> currentPowerList = new ArrayList<>();
        currentPowerList.add(new CurrentPower(1761429600, 345, -648));
        currentPowerList.add(new CurrentPower(1761429601, 346, -638));
        currentPowerList.add(new CurrentPower(1761429602, 347, -628));

        long fromEpochSeconds = 1761429600;
        long toEpochSeconds = 1761430000;
        when(currentPowerDao.getCurrentPowers(fromEpochSeconds, toEpochSeconds)).thenReturn(currentPowerList);

        // When
        mockMvc.perform(get("/current-power-surplus")
                .param("fromEpochSeconds", Long.toString(fromEpochSeconds))
                .param("toEpochSeconds", Long.toString(toEpochSeconds)))
        // Then
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value("-281.0"));
    }

    @Test
    void givenCurrentPowerList_whenCallStatsEndpoint_thenCorrectStatsReturned() throws Exception {
        // Given
        List<CurrentPower> currentPowerList = new ArrayList<>();
        currentPowerList.add(new CurrentPower(1761429600, 345, -648));
        currentPowerList.add(new CurrentPower(1761429601, 346, -638));
        currentPowerList.add(new CurrentPower(1761429602, 347, -228));

        long fromEpochSeconds = 1761429600;
        long toEpochSeconds = 1761430000;
        when(currentPowerDao.getCurrentPowers(fromEpochSeconds, toEpochSeconds)).thenReturn(currentPowerList);

        // When
        mockMvc.perform(get("/stats")
                        .param("fromEpochSeconds", Long.toString(fromEpochSeconds))
                        .param("toEpochSeconds", Long.toString(toEpochSeconds)))
                // Then
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.totalGeneration").value("1038.0"),
                        jsonPath("$.totalConsumption").value("-1514.0"),
                        jsonPath("$.totalExported").value("119.0"),
                        jsonPath("$.totalImported").value("-595.0")
                );
    }
}
