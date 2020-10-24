package com.example.app.web;

import com.example.app.FuelConsumptionApplication;
import com.example.app.entity.Record;
import com.example.app.entity.RecordStatistics;
import com.example.app.service.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FuelConsumptionApplication.class
)
@AutoConfigureMockMvc
class RecordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @LocalServerPort
    private int port;
    @MockBean
    private RecordService recordService;
    private Record record;
    private RecordStatistics recordStatistics;
    private String url;

    @BeforeEach
    void setUp() {
        record = createRecord();
        recordStatistics = createRecordStatistics();
        url = "http://localhost:" + port + "/api/v1/records";
    }

    @Test
    public void shouldSave() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(record))
        ).andDo(print()).andExpect(status().isCreated());
        verify(recordService, times(1)).save(record);
    }

    @Test
    public void shouldGetAll() throws Exception {
        List<Record> records = List.of(record);
        when(recordService.getAll()).thenReturn(records);
        String json = mapper.writeValueAsString(records);
        mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk()).andExpect(content().string(json));
        verify(recordService, times(1)).getAll();
    }

    @Test
    public void shouldGetMoneyPerMonth() throws Exception {
        Map<String, BigDecimal> map = new HashMap<>();
        map.put(Month.SEPTEMBER.toString(), BigDecimal.valueOf(100.00));
        when(recordService.getMoneyPerMonth(Optional.empty())).thenReturn(map);
        String json = mapper.writeValueAsString(map);
        System.out.println(json);
        mockMvc.perform(
                MockMvcRequestBuilders.get(url + "/money_spent")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk()).andExpect(content().string(json));
        verify(recordService, times(1)).getMoneyPerMonth(Optional.empty());
    }

    @Test
    public void shouldReturnRecordsPerMonth() throws Exception {
        List<Record> records = List.of(record);
        when(recordService.getForSpecifiedMonth(Month.OCTOBER.toString(), Optional.empty())).thenReturn(records);
        String json = mapper.writeValueAsString(records);
        mockMvc.perform(
                MockMvcRequestBuilders.get(url + "/OCTOBER")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk()).andExpect(content().string(json));
        verify(recordService, times(1))
                .getForSpecifiedMonth(Month.OCTOBER.toString(), Optional.empty());
    }

    @Test
    public void shouldReturnStatistics() throws Exception {
        List<RecordStatistics> recordStatisticsList = List.of(recordStatistics);
        when(recordService.getStatistics(Optional.empty())).thenReturn(recordStatisticsList);
        String json = mapper.writeValueAsString(recordStatisticsList);
        mockMvc.perform(
                MockMvcRequestBuilders.get(url + "/statistics")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk()).andExpect(content().string(json));
        verify(recordService, times(1)).getStatistics(Optional.empty());
    }


    private Record createRecord() {
        Record record = new Record();
        record.setId(0);
        record.setFuelType("95");
        record.setPricePerLiter(BigDecimal.valueOf(1.25));
        record.setVolume(20);
        record.setDate(LocalDate.now());
        record.setDriverId(1);
        return record;
    }

    private RecordStatistics createRecordStatistics() {
        RecordStatistics recordStatistics = new RecordStatistics();
        recordStatistics.setMonth(Month.OCTOBER);
        recordStatistics.setVolume(40);
        recordStatistics.setAveragePrice(BigDecimal.valueOf(25.00));
        recordStatistics.setTotalPrice(BigDecimal.valueOf(50.00));
        recordStatistics.setFuelType("98");
        return recordStatistics;
    }
}