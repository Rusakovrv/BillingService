package com.test3.service3.controller;

import com.test3.service3.model.Sim;
import com.test3.service3.model.SimPackage;
import com.test3.service3.service.SimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 
 * @author rusakov_r_v
 */
@RestController
public class Controller {

    @Autowired
    private SimService simService;

    /**
     * 
     * @param num
     * @return 
     */
    @PutMapping(value = "/activate/{num}")
    public Sim activate(@PathVariable("num") long num) {
        return simService.activateSim(num, true);
    }

    @PutMapping(value = "/deactivate/{num}")
    public Sim deactivate(@PathVariable("num") long num) {
        return simService.activateSim(num, false);
    }

    @GetMapping(value = "/minutes/{num}")
    public SimPackage minutes(@PathVariable("num") long num) {
        return simService.fetchMinutes(num);
    }

    @GetMapping(value = "/megabytes/{num}")
    public SimPackage megabytes(@PathVariable("num") long num) {
        return simService.fetchMegabytes(num);
    }

    @PutMapping(value = "/addminutes/{num}")
    public Sim addMinutes(@PathVariable("num") long num, @RequestBody SimPackage pac) {
        return simService.addMinutesPackage(num, pac);
    }

    @PutMapping(value = "/addmegabytes/{num}")
    public Sim addMegabytes(@PathVariable("num") long num, @RequestBody SimPackage pac) {
        return simService.addMegabytesPackage(num, pac);
    }

    @PutMapping(value = "/writeoffminutes/{num}")
    public Sim writeOffMinutes(@PathVariable("num") long num, @RequestParam int value) {
        return simService.writeOffMinutes(num, value);
    }

    @PutMapping(value = "/writeoffmegabytes/{num}")
    public Sim writeOffMegabytes(@PathVariable("num") long num, @RequestParam int value) {
        return simService.writeOffMegabytes(num, value);
    }

//    @ExceptionHandler(NumberNotFoundException.class)
//    protected ResponseEntity<Object> handleEntityNotFoundEx(NumberNotFoundException ex, WebRequest request) {
//
//        CustomError error = new CustomError(HttpStatus.NOT_FOUND, "entity not found ex", ex);
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
    //@PutMapping( value = "/minutes")
    //public ResponseEntity<Long> addpackage(@RequestBody Package pac){
    //    long id=simService.addPackage(pac);
    //    return new ResponseEntity<Long>(id,HttpStatus.OK);
    // }
}
