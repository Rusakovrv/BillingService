/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test3.service3.model;

import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

/**
 *
 * @author rusakov_r_v
 */
@Service
public class SimDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean activateNumber(Long number, boolean status) {
        int c = jdbcTemplate.update("UPDATE sim  SET status = " + Boolean.toString(status) + " where number=" + number.toString());
        return c > 0;
    }

    public void changeRemains(Long number, int value, boolean isMinutes) {
        String type=isMinutes? "minutes": "megabytes";
        jdbcTemplate.update("UPDATE sim  SET " + type + " = " + value +" where number=" + number.toString());
    }

    public boolean isActive(Long number) {
        String sql = "SELECT status FROM sim where number=?";
        Boolean c = jdbcTemplate.queryForObject(sql, new Object[]{number}, Boolean.class);
        if (c != null) {
            return c;
        } else {
            return false;
        }
    }

    public boolean isExist(long number){
        String sql = "SELECT count(*) FROM sim where number=?";
        SqlRowSet rs=jdbcTemplate.queryForRowSet(sql,new Object[]{number});
        rs.next();
        if(rs.getInt(1)>0){
            return true;
        }
        else{
            return false;
        }
    }

    public Sim fetchSimByNumber(long number) {
        String sql = "SELECT * FROM sim where number=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{number}, (rs, rowNum)
                -> new Sim(rs.getLong("number"),
                        rs.getBoolean("status"), new SimPackage(rs.getInt("minutes"),
                        rs.getDate("minutesExpiration")), new SimPackage(rs.getInt("megabytes"),
                        rs.getDate("megabytesExpiration"))));
    }

    private SimPackage fetchPackageByNumber(long number, String packType) {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(packType).append(", ").append(packType).append("Expiration FROM sim where number=?");
        return jdbcTemplate.queryForObject(sql.toString(), new Object[]{number}, (rs, rowNum)
                -> new SimPackage(rs.getInt(packType), rs.getDate(packType + "Expiration")));
    }

    public SimPackage fetchMinutesByNumber(long number) {
        return fetchPackageByNumber(number, "minutes");
    }

    public SimPackage fetchMegabytesByNumber(long number) {
        return fetchPackageByNumber(number, "megabytes");
    }

//    public Sim consume(long number, String packageType) {
//        SimPackage currPack=fetchPackageByNumber(number, packageType);
//        if(new Date().after(currPack.getExp())){
//            
//        }
//
//    }
    private Sim addPackage(long number, SimPackage pac, String packageType) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String sql = "UPDATE sim  SET " + packageType + " = "
                    + pac.getValue() + " , " + packageType + "Expiration = '"
                    + format.format(pac.getExp()) + "' where number=" + Long.toString(number);
            jdbcTemplate.update(sql);
            return fetchSimByNumber(number);

    }

    public Sim addMinutesPackage(long number, SimPackage pac) {
       return addPackage(number, pac, "minutes");
    }

    public Sim addMegabytesPackage(long number, SimPackage pac) {
        return addPackage(number, pac, "megabytes");
        }

}
