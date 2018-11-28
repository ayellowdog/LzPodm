package com.inspur.podm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspur.podm.common.persistence.entity.Chassis;
import com.inspur.podm.service.dao.MyChassisDao;


@RestController
@RequestMapping(value = "test")
public class PerconController {

	@Autowired
	MyChassisDao myDao;

    @PostMapping
    public void addPerson() {
    	Chassis c = new Chassis();
    	c.setDescription("lalalala");
    	myDao.save(c);
    }

    
}

