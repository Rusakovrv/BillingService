package com.test3.service3.service;

import com.test3.service3.model.Sim;
import com.test3.service3.model.SimPackage;

import java.util.List;

public interface SimService {

    public Sim addMinutesPackage(long number, SimPackage p);

    public Sim addMegabytesPackage(long number, SimPackage p);

    public Sim  writeOffMinutes(long number, int minutes);

    public Sim  writeOffMegabytes(long number, int data);

    public SimPackage fetchMinutes(long number);

    public SimPackage fetchMegabytes(long number);

    public Sim activateSim(long number, boolean status);


}
