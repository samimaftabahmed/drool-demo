package com.zaloni.drooldemo;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.*;

@RestController
public class OfferController {

    @Autowired
    @Qualifier("kieSession")
    private KieSession kieSession;

    @Autowired
    @Qualifier("persistentSession")
    private KieSession persistentSession;

    @GetMapping("/order/{card-type}/{price}")
    public Order order(@PathVariable("card-type") String cardType, @PathVariable int price) {

        Order order = new Order(cardType, price);
        kieSession.insert(order);
        kieSession.fireAllRules();
        return order;
    }

    @GetMapping("/order-p/{card-type}/{price}")
    public Order order2(@PathVariable("card-type") String cardType, @PathVariable int price)
            throws NamingException, SystemException, NotSupportedException, HeuristicRollbackException,
            HeuristicMixedException, RollbackException {

        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();
        Order order = new Order(cardType, price);
        persistentSession.insert(order);
        persistentSession.startProcess("process1");
        ut.commit();
        return order;
    }

}
