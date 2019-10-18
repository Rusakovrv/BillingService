package com.test3.service3.model;

public class Sim {

    private long number;
    private boolean status;
    private SimPackage minutes;
    private SimPackage megabytes;

    public Sim() {
    }

    public long getNumber() {
        return number;
    }

    public Sim(long number, boolean status, SimPackage minutes, SimPackage megabytes) {
        this.number = number;
        this.status = status;
        this.minutes = minutes;
        this.megabytes = megabytes;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SimPackage getMinutes() {
        return minutes;
    }

    public void setMinutes(SimPackage minutes) {
        this.minutes = minutes;
    }

    public SimPackage getMegabytes() {
        return megabytes;
    }

    public void setMegabytes(SimPackage megabytes) {
        this.megabytes = megabytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sim)) return false;
        Sim sim = (Sim) o;
        return number == sim.number &&
                status == sim.status &&
                minutes.equals(sim.minutes) &&
                megabytes.equals(sim.megabytes);
    }

}
