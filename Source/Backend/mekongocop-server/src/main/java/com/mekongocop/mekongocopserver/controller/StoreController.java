package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.repository.StoreRepository;
import com.mekongocop.mekongocopserver.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class StoreController {
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreRepository storeRepository;



}
