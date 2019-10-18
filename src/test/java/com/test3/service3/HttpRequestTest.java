package com.test3.service3;

import com.test3.service3.model.Sim;
import com.test3.service3.model.SimPackage;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    private final static long TEST_NUMBER_1 = 89995678324L;
    private final static long TEST_NUMBER_2 = 89992739472L;
    private final static long TEST_NUMBER_3 = 89995645731L;
    private final static long TEST_NUMBER_WRONG_DATES = 89994567583L;
    private final static long TEST_NUMBER_NOT_ACTIVE = 89992345362l;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void gettingMinutesByNumber() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port
                + "/minutes/" + TEST_NUMBER_1,
                SimPackage.class).getValue(), equalTo(10));
    }

    @Test
    public void gettingMegabytesByNumber() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port
                + "/megabytes/" + TEST_NUMBER_1,
                SimPackage.class).getValue(), equalTo(3245123));
    }

    @Test
    public void activateSim() throws Exception {
        assertThat(this.restTemplate.exchange("http://localhost:" + port
                + "/activate/" + TEST_NUMBER_2, HttpMethod.PUT, null, Sim.class).getBody().isStatus(), equalTo(true));
    }

    @Test
    public void deActivateSim() throws Exception {
        assertThat(this.restTemplate.exchange("http://localhost:" + port
                + "/deactivate/" + TEST_NUMBER_2, HttpMethod.PUT, null, Sim.class).getBody().isStatus(), equalTo(false));
    }

    @Test
    public void addMinutesPackage() throws Exception {
        SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ResponseEntity<String> simEntity = this.restTemplate.exchange("http://localhost:" + port
                + "/addminutes/" + TEST_NUMBER_3, HttpMethod.PUT,
                new HttpEntity<>(new SimPackage(10, DATE_FORMATTER.parse("2020-01-10"))),
                String.class);
        assertThat(simEntity.getBody(), containsString("\"minutes\":{\"value\":10"));
    }

    @Test
    public void addMegabytesPackage() throws Exception {
        SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ResponseEntity<String> simEntity = this.restTemplate.exchange("http://localhost:" + port
                + "/addmegabytes/" + TEST_NUMBER_3, HttpMethod.PUT,
                new HttpEntity<>(new SimPackage(10, DATE_FORMATTER.parse("2020-01-10"))),
                String.class);
        assertThat(simEntity.getBody(), containsString("\"megabytes\":{\"value\":10"));
    }

    @Test
    public void addMegabytesInvalidPackage() throws Exception {
        SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ResponseEntity<String> simEntity = this.restTemplate.exchange("http://localhost:" + port
                        + "/addmegabytes/" + TEST_NUMBER_3, HttpMethod.PUT,
                new HttpEntity<>(new SimPackage(-4, DATE_FORMATTER.parse("2020-01-10"))),
                String.class);
        assertThat(simEntity.getBody(), containsString("package with  -4MB  and expiration date 2020-01-10 is invalid"));
    }

    @Test
    public void addMegabytesPackageToDisabledSim() throws Exception {
        SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ResponseEntity<String> simEntity = this.restTemplate.exchange("http://localhost:" + port
                        + "/addmegabytes/" + TEST_NUMBER_NOT_ACTIVE, HttpMethod.PUT,
                new HttpEntity<>(new SimPackage(-4, DATE_FORMATTER.parse("2020-01-10"))),
                String.class);
        assertThat(simEntity.getBody(), containsString("SIM card with number " + TEST_NUMBER_NOT_ACTIVE + " is  not active"));
    }

    @Test
    public void writeOffMegabytesPackage() throws Exception {
        int spentValue = 1;
        int currentMegabytes = this.restTemplate.getForObject("http://localhost:" + port
                + "/minutes/" + TEST_NUMBER_3,
                SimPackage.class).getValue();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:" + port
                + "/writeoffmegabytes/" + TEST_NUMBER_3).queryParam("value", Integer.toString(spentValue));
        ResponseEntity<String> simEntity2 = this.restTemplate.exchange(builder.toUriString(), HttpMethod.PUT,
                null, String.class);
        assertThat(simEntity2.getBody(), containsString("\"megabytes\":{\"value\":" + (currentMegabytes - spentValue)));
    }

    @Test
    public void writeOffMinutesPackage() throws Exception {
        int spentValue = 1;
        int currentMinutes = this.restTemplate.getForObject("http://localhost:" + port
                + "/minutes/" + TEST_NUMBER_3,
                SimPackage.class).getValue();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:" + port
                + "/writeoffminutes/" + TEST_NUMBER_3).queryParam("value", Integer.toString(spentValue));
        ResponseEntity<String> simEntity2 = this.restTemplate.exchange(builder.toUriString(), HttpMethod.PUT,
                null, String.class);
        assertThat(simEntity2.getBody(), containsString("\"minutes\":{\"value\":" + (currentMinutes - spentValue)));
    }

    @Test
    public void writeOffMinutesPackageOutOfResource() throws Exception {
        int currentMinutes = this.restTemplate.getForObject("http://localhost:" + port
                + "/minutes/" + TEST_NUMBER_3,
                SimPackage.class).getValue();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:" + port
                + "/writeoffminutes/" + TEST_NUMBER_3).queryParam("value", Integer.toString(currentMinutes + 10));
        ResponseEntity<String> simEntity2 = this.restTemplate.exchange(builder.toUriString(), HttpMethod.PUT,
                null, String.class);
        assertThat(simEntity2.getBody(), containsString("\"status\":\"BAD_REQUEST\""));
        assertThat(simEntity2.getBody(), containsString("Not enough minutes"));
    }

    @Test
    public void writeOffMegabytesPackageWithNullDate() throws Exception {
        int spentValue = 1000;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:" + port
                + "/writeoffmegabytes/" + TEST_NUMBER_WRONG_DATES).queryParam("value", Integer.toString(spentValue));
        ResponseEntity<String> simEntity2 = this.restTemplate.exchange(builder.toUriString(), HttpMethod.PUT,
                null, String.class);
        assertThat(simEntity2.getBody(), containsString("\"status\":\"BAD_REQUEST\""));
        assertThat(simEntity2.getBody(), containsString("Package of megabytes is not set"));
    }

    @Test
    public void writeOffMinutesPackageWithExpiredDate() throws Exception {
        int spentValue = 1;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:" + port
                + "/writeoffminutes/" + TEST_NUMBER_WRONG_DATES).queryParam("value", Integer.toString(spentValue));
        ResponseEntity<String> simEntity2 = this.restTemplate.exchange(builder.toUriString(), HttpMethod.PUT,
                null, String.class);
        assertThat(simEntity2.getBody(), containsString("\"status\":\"BAD_REQUEST\""));
        assertThat(simEntity2.getBody(), containsString("Package of minutes is expired"));
    }


    @Test
    public void wrongNumberInRequest() throws Exception {
        String wrongNumber = "123432464365";
        assertThat(this.restTemplate.getForObject("http://localhost:" + port
                        + "/minutes/" + wrongNumber,
                String.class), containsString("\"message\":\"Number " + wrongNumber + " was not found\""));
    }


}
