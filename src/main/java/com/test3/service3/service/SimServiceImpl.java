package com.test3.service3.service;

import com.test3.service3.exception.*;
import com.test3.service3.model.Sim;
import com.test3.service3.model.SimDAO;
import com.test3.service3.model.SimPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.annotation.PostConstruct;

@Service
public class SimServiceImpl implements SimService {

//    @Autowired
//    JdbcTemplate jdbcTemplate;
    @Autowired
    SimDAO simDAO;

//    public int addPackage(Package p);
//
//    public Sim consumeMinutes(Long number);
//
//    public Sim consumeData(Long number);
//

    @Override
    public SimPackage fetchMinutes(long number) {
        return fetchPackage(number, true);

    }

    @Override
    public SimPackage fetchMegabytes(long number) {
        return fetchPackage(number, false);

    }

    private SimPackage fetchPackage(long number, boolean isMinutes) {
        validateNumber(number);
        if (isMinutes) {
            return simDAO.fetchMinutesByNumber(number);
        } else {
            return simDAO.fetchMegabytesByNumber(number);
        }

    }

    @Override
    public Sim activateSim(long number, boolean status) {
        boolean c = simDAO.activateNumber(number, status);
        if (c) {
            return simDAO.fetchSimByNumber(number);
        } else {
            throw new NumberNotFoundException("Number " + number + " was not found");
        }
    }
//

    @Override
        public Sim addMinutesPackage(long number, SimPackage pac) {
        return addPackage(number, pac, true);
    }

    @Override
    public Sim addMegabytesPackage(long number, SimPackage pac) {
        return addPackage(number, pac, false);
    }

    @Override
    public Sim writeOffMinutes(long number, int value) {
        return writeOff(number, value, "minutes");
    }

    @Override
    public Sim writeOffMegabytes(long number, int value) {
        return writeOff(number, value, "megabytes");
    }

    private Sim writeOff(long number, int value, String type) {
        if (value <= 0) {
            throw new IncorrectPackageDataException("Can not write off  " + value + " from" + type);
        }
        validateNumber(number);
        SimPackage pac = "minutes".equals(type) ? simDAO.fetchMinutesByNumber(number) : simDAO.fetchMegabytesByNumber(number);
        if (pac.getExp() == null) {
            throw new NotEnoughPackageResourcesException("Package of " + type + " is not set");
        }

        if (pac.getExp().before(new Date())) {
            simDAO.changeRemains(number, 0, "minutes".equals(type));
            throw new NotEnoughPackageResourcesException("Package of " + type + " is expired");
        }
        if (pac.getValue() < value) {
            throw new NotEnoughPackageResourcesException("Not enough " + type + ". " + pac.getValue() + " left");
        }
        simDAO.changeRemains(number, pac.getValue() - value, "minutes".equals(type));
        return simDAO.fetchSimByNumber(number);
    }

    private Sim addPackage(long number, SimPackage pac, boolean isMinutes) {
        validateNumber(number);
        if (pac.getValue() > 0 && pac.getExp() != null && pac.getExp().after(new Date())) {
            return (isMinutes ? simDAO.addMinutesPackage(number, pac) : simDAO.addMegabytesPackage(number, pac));
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            throw new PackageNotValidException("package with  " + pac.getValue() + (!isMinutes ? "MB " : "min ")
                    + " and expiration date " + (pac.getExp() != null ? format.format(pac.getExp()) : "null") + " is invalid");
        }

    }

    private void validateNumber(long number) {
        if (!simDAO.isExist(number)) {
            throw new NumberNotFoundException("Number " + number + " was not found");
        }
        if (!simDAO.isActive(number)) {
            throw new SimNotActiveException("SIM card with number " + number + " is  not active");
        }
    }

//    @Override
//    public Sim consumeData(Long number) {
//        
//    }
}
