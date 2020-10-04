package com.zaloni.drooldemo;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OfferController {

    @Autowired
    private KieSession kieSession;

    @PostMapping("/order")
    public Order order(@RequestBody Order order) {
        kieSession.insert(order);
        kieSession.fireAllRules();
        return order;
    }
}
