package com.test3.service3;

import com.test3.service3.controller.Controller;
import com.test3.service3.exception.NotEnoughPackageResourcesException;
import com.test3.service3.exception.NumberNotFoundException;
import com.test3.service3.exception.PackageNotValidException;
import com.test3.service3.exception.SimNotActiveException;
import com.test3.service3.model.Sim;
import com.test3.service3.model.SimDAO;
import com.test3.service3.model.SimPackage;
import com.test3.service3.service.SimService;
import com.test3.service3.service.SimServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimServiceTest {
    private final static long TEST_NUMBER = 89992739472L;
    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Mock
    private SimDAO simDAOMock;

    @InjectMocks
    SimServiceImpl simServiceImpl;


    @Test
    public void activateSimByValidNumber() throws Exception {
        Sim simres=new Sim(TEST_NUMBER, true, new SimPackage(24,DATE_FORMATTER.parse("2021-01-01")),new SimPackage(11,DATE_FORMATTER.parse("2022-01-01")));
        when(simDAOMock.activateNumber(TEST_NUMBER, true)).thenReturn(true);
        when(simDAOMock.fetchSimByNumber(TEST_NUMBER)).thenReturn(simres);
        assertThat(simServiceImpl.activateSim(TEST_NUMBER,true), equalTo(simres));
    }


    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Test
    public void activateSimByInvalidNumber() throws Exception {
        when(simDAOMock.activateNumber(TEST_NUMBER, true)).thenReturn(false);
        exceptionRule.expect(NumberNotFoundException.class);
        exceptionRule.expectMessage("Number " + TEST_NUMBER + " was not found");
        simServiceImpl.activateSim(TEST_NUMBER,true);
    }

    @Test
    public void deActivateSimByValidNumber() throws Exception {
        Sim simres=new Sim(TEST_NUMBER, false, new SimPackage(24,DATE_FORMATTER.parse("2021-01-01")),new SimPackage(11,DATE_FORMATTER.parse("2022-01-01")));
        when(simDAOMock.activateNumber(TEST_NUMBER, false)).thenReturn(true);
        when(simDAOMock.fetchSimByNumber(TEST_NUMBER)).thenReturn(simres);
        assertThat(simServiceImpl.activateSim(TEST_NUMBER,false), equalTo(simres));
    }

    @Test
    public void addMinutesPackageByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2022-01-01"));
        Sim simres=new Sim(TEST_NUMBER, false, new SimPackage(24,DATE_FORMATTER.parse("2021-01-01")),new SimPackage(11,DATE_FORMATTER.parse("2022-01-01")));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.addMinutesPackage(TEST_NUMBER, sm)).thenReturn(simres);
        assertThat(simServiceImpl.addMinutesPackage(TEST_NUMBER,sm), equalTo(simres));
    }

    @Test
    public void addMegabytesPackageByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2022-01-01"));
        Sim simres=new Sim(TEST_NUMBER, false, new SimPackage(24,DATE_FORMATTER.parse("2021-01-01")),sm);
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.addMegabytesPackage(TEST_NUMBER, sm)).thenReturn(simres);
        assertThat(simServiceImpl.addMegabytesPackage(TEST_NUMBER,sm), equalTo(simres));
    }


    @Test
    public void addMegabytesPackageByInactiveValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2022-01-01"));
        Sim simres=new Sim(TEST_NUMBER, false, new SimPackage(24,DATE_FORMATTER.parse("2021-01-01")),new SimPackage(11,DATE_FORMATTER.parse("2022-01-01")));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(false);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        exceptionRule.expect(SimNotActiveException.class);
        exceptionRule.expectMessage("SIM card with number " + TEST_NUMBER + " is  not active");
        simServiceImpl.addMegabytesPackage(TEST_NUMBER,sm);
    }

    @Test
    public void addInvalidMegabytesPackageByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2008-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        exceptionRule.expect(PackageNotValidException.class);
        exceptionRule.expectMessage("package with  " + sm.getValue() + "MB  and expiration date " + DATE_FORMATTER.format(sm.getExp())  + " is invalid");
        simServiceImpl.addMegabytesPackage(TEST_NUMBER,sm);
    }

    @Test
    public void addInvalidMinutesPackageByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2008-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        exceptionRule.expect(PackageNotValidException.class);
        exceptionRule.expectMessage("package with  " + sm.getValue() + "min  and expiration date " + DATE_FORMATTER.format(sm.getExp())  + " is invalid");
        simServiceImpl.addMinutesPackage(TEST_NUMBER,sm);
    }

    @Test
    public void getMinutesPackageByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2020-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMinutesByNumber(TEST_NUMBER)).thenReturn(sm);
        assertThat(simServiceImpl.fetchMinutes(TEST_NUMBER), equalTo(sm));
    }

    @Test
    public void getMegabytesPackageByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,DATE_FORMATTER.parse("2020-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMegabytesByNumber(TEST_NUMBER)).thenReturn(sm);
        assertThat(simServiceImpl.fetchMegabytes(TEST_NUMBER), equalTo(sm));
    }

    @Test
    public void getMegabytesPackageWithNullDateByValidNumber() throws Exception {
        SimPackage sm=new SimPackage(11,null);
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMegabytesByNumber(TEST_NUMBER)).thenReturn(sm);
        assertThat(simServiceImpl.fetchMegabytes(TEST_NUMBER), equalTo(sm));
    }

    @Test
    public void writeOffMinutesByValidNumber() throws Exception {
        int value=1;
        SimPackage sm1=new SimPackage(11,DATE_FORMATTER.parse("2022-01-01"));
        SimPackage sm2=new SimPackage(11-value,DATE_FORMATTER.parse("2022-01-01"));
        Sim simres=new Sim(TEST_NUMBER, true, sm1,sm1);
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMinutesByNumber(TEST_NUMBER)).thenReturn(sm1);

        when(simDAOMock.fetchSimByNumber(TEST_NUMBER)).thenReturn(simres);
        assertThat(simServiceImpl.writeOffMinutes(TEST_NUMBER, value), equalTo(simres));
    }

    @Test
    public void writeOffMegabytesByValidNumber() throws Exception {
        int value=1;
        SimPackage sm1=new SimPackage(11,DATE_FORMATTER.parse("2022-01-01"));
        SimPackage sm2=new SimPackage(11-value,DATE_FORMATTER.parse("2022-01-01"));
        Sim simres=new Sim(TEST_NUMBER, true, sm2,sm1);
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMegabytesByNumber(TEST_NUMBER)).thenReturn(sm1);
        when(simDAOMock.fetchSimByNumber(TEST_NUMBER)).thenReturn(simres);
        assertThat(simServiceImpl.writeOffMegabytes(TEST_NUMBER, value), equalTo(simres));
    }

    @Test
    public void writeOffMegabytesFromExpiredPackageByValidNumber() throws Exception {
        int value=1;
        SimPackage sm1=new SimPackage(11,DATE_FORMATTER.parse("2008-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMegabytesByNumber(TEST_NUMBER)).thenReturn(sm1);
        exceptionRule.expect(NotEnoughPackageResourcesException.class);
        exceptionRule.expectMessage("Package of megabytes is expired");
        simServiceImpl.writeOffMegabytes(TEST_NUMBER,value);
    }

    @Test
    public void writeOffMinutesFromExpiredPackageByValidNumber() throws Exception {
        int value=1;
        SimPackage sm1=new SimPackage(11,DATE_FORMATTER.parse("2008-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMinutesByNumber(TEST_NUMBER)).thenReturn(sm1);
        exceptionRule.expect(NotEnoughPackageResourcesException.class);
        exceptionRule.expectMessage("Package of minutes is expired");
        simServiceImpl.writeOffMinutes(TEST_NUMBER,value);
    }

    @Test
    public void writeOffTooManyMinutesByValidNumber() throws Exception {
        int value=111;
        SimPackage sm1=new SimPackage(11,DATE_FORMATTER.parse("2020-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMinutesByNumber(TEST_NUMBER)).thenReturn(sm1);
        exceptionRule.expect(NotEnoughPackageResourcesException.class);
        exceptionRule.expectMessage("Not enough minutes. " + sm1.getValue() + " left");
        simServiceImpl.writeOffMinutes(TEST_NUMBER,value);
    }

    @Test
    public void writeOffTooManyMegabytesyValidNumber() throws Exception {
        int value=111;
        SimPackage sm1=new SimPackage(11,DATE_FORMATTER.parse("2020-01-01"));
        when(simDAOMock.isActive(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.isExist(TEST_NUMBER)).thenReturn(true);
        when(simDAOMock.fetchMegabytesByNumber(TEST_NUMBER)).thenReturn(sm1);
        exceptionRule.expect(NotEnoughPackageResourcesException.class);
        exceptionRule.expectMessage("Not enough megabytes. " + sm1.getValue() + " left");
        simServiceImpl.writeOffMegabytes(TEST_NUMBER,value);
    }

}
