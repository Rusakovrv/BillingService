package com.test3.service3;

import com.test3.service3.controller.Controller;
import com.test3.service3.model.Sim;
import com.test3.service3.model.SimPackage;
import com.test3.service3.service.SimService;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
public class ControllerTest {

    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final static long TEST_NUMBER = 89992739472L;
    static{
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT+3"));
    }
    @Autowired
    private MockMvc mvc;

    @MockBean
    private SimService simServiceMock;

    @Test
    public void activateSimByNumber() throws Exception {

        Sim test_sim = new Sim(TEST_NUMBER, true,
                new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")),
                new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")));

        when(simServiceMock.activateSim(TEST_NUMBER, true)).thenReturn(test_sim);
        mvc.perform(put("/activate/" + Long.toString(TEST_NUMBER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(TEST_NUMBER)))
                .andExpect(jsonPath("$.status", is(true)));
        verify(simServiceMock).activateSim(TEST_NUMBER,true);
    }


    @Test
    public void deActivateSimByNumber() throws Exception {
        Sim test_sim = new Sim(TEST_NUMBER, false,
                new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")),
                new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")));
        when(simServiceMock.activateSim(TEST_NUMBER, false)).thenReturn(test_sim);

        mvc.perform(put("/deactivate/" + Long.toString(TEST_NUMBER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(TEST_NUMBER)))
                .andExpect(jsonPath("$.status", is(false)));
        verify(simServiceMock).activateSim(TEST_NUMBER,false);
    }

    @Test
    public void getMinutesByNumber() throws Exception {
        when(simServiceMock.fetchMinutes(TEST_NUMBER))
                .thenReturn(new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")));
        mvc.perform(get("/minutes/" + Long.toString(TEST_NUMBER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(1)));
        verify(simServiceMock).fetchMinutes(TEST_NUMBER);
    }

    @Test
    public void getMegabytesByNumber() throws Exception {
        when(simServiceMock.fetchMegabytes(TEST_NUMBER))
                .thenReturn(new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")));
        mvc.perform(get("/megabytes/" + Long.toString(TEST_NUMBER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(1)));
        verify(simServiceMock).fetchMegabytes(TEST_NUMBER);
    }

    
    
    @Test
    public void addMegabytesToSim() throws Exception {
        Sim test_sim = new Sim(TEST_NUMBER, true,
                new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")),
                new SimPackage(20,  DATE_FORMATTER.parse("2020-01-10")));
        when(simServiceMock.addMegabytesPackage(anyLong(),any(SimPackage.class))).
                thenReturn(test_sim);
        mvc.perform(put("/addmegabytes/" + Long.toString(TEST_NUMBER))
                .content("{\"value\":20,\"exp\":\"2020-01-10\"}").characterEncoding("UTF-8").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.megabytes.value", is(20)))
                .andExpect(jsonPath("$.minutes.value", is(1)))
                .andExpect(jsonPath("$.minutes.exp", is("2018-01-10")))
                .andExpect(jsonPath("$.megabytes.exp", is("2020-01-10")));
        verify(simServiceMock).addMegabytesPackage(TEST_NUMBER,new SimPackage(20, DATE_FORMATTER.parse("2020-01-10")));
    }

    @Test
    public void addMinutesToSim() throws Exception {
        Sim test_sim = new Sim(TEST_NUMBER, true,
                new SimPackage(20, DATE_FORMATTER.parse("2020-01-10")),
                new SimPackage(1,  DATE_FORMATTER.parse("2018-01-10")));
        when(simServiceMock.addMinutesPackage(anyLong(),any(SimPackage.class))).
                thenReturn(test_sim);
        mvc.perform(put("/addminutes/" + Long.toString(TEST_NUMBER))
                .content("{\"value\":20,\"exp\":\"2020-01-10\"}").characterEncoding("UTF-8")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.megabytes.value", is(1)))
                .andExpect(jsonPath("$.minutes.value", is(20)))
                .andExpect(jsonPath("$.minutes.exp", is("2020-01-10")))
                .andExpect(jsonPath("$.megabytes.exp", is("2018-01-10")));
        verify(simServiceMock).addMinutesPackage(TEST_NUMBER,new SimPackage(20, DATE_FORMATTER.parse("2020-01-10")));
    }

    @Test
    public void writeOffMinutesFromSim() throws Exception {
        Sim test_sim = new Sim(TEST_NUMBER, true,
                new SimPackage(10, DATE_FORMATTER.parse("2020-01-10")),
                new SimPackage(1,  DATE_FORMATTER.parse("2018-01-10")));
        when(simServiceMock.writeOffMinutes(TEST_NUMBER,10)).thenReturn(test_sim);
        mvc.perform(put("/writeoffminutes/" + Long.toString(TEST_NUMBER)).param("value","10")
        .accept(APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andExpect(jsonPath("$.megabytes.value", is(1)))
                .andExpect(jsonPath("$.minutes.value", is(10)))
                .andExpect(jsonPath("$.minutes.exp", is("2020-01-10")))
                .andExpect(jsonPath("$.megabytes.exp", is("2018-01-10")));
        verify(simServiceMock).writeOffMinutes(TEST_NUMBER,10);
    }

    @Test
    public void writeOffMegabytesFromSim() throws Exception {
        Sim test_sim = new Sim(TEST_NUMBER, true,
                new SimPackage(1, DATE_FORMATTER.parse("2018-01-10")),
                new SimPackage(10,  DATE_FORMATTER.parse("2020-01-10")));
        when(simServiceMock.writeOffMegabytes(TEST_NUMBER,10)).thenReturn(test_sim);
        mvc.perform(put("/writeoffmegabytes/" + Long.toString(TEST_NUMBER)).param("value","10")
                .accept(APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andExpect(jsonPath("$.megabytes.value", is(10)))
                .andExpect(jsonPath("$.minutes.value", is(1)))
                .andExpect(jsonPath("$.minutes.exp", is("2018-01-10")))
                .andExpect(jsonPath("$.megabytes.exp", is("2020-01-10")));
        verify(simServiceMock).writeOffMegabytes(TEST_NUMBER,10);
    }



}
