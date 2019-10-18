package com.test3.service3.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;

public class SimPackage {

    private int value;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date exp;

    public SimPackage() {
    }

    public SimPackage(int value, Date exp) {
        this.value = value;
        this.exp = exp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        SimPackage pac=(SimPackage) obj;
        return value==pac.getValue() && (Objects.equals(exp, pac.exp));
    }

}
