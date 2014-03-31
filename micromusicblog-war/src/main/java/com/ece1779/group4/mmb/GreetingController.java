package com.ece1779.group4.mmb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller   
@RequestMapping("/greeting")
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Greeting> getGreetingAll(){
    	 Greeting g =  new Greeting(counter.incrementAndGet(),
               String.format(template, "world"));
    	 List<Greeting> result = new ArrayList<Greeting>(); 
    	 result.add(g);
    	 return result;
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public  ResponseEntity<Greeting> getGreeting( @PathVariable String id) {
//      return new Greeting(counter.incrementAndGet(),
//      String.format(template, name));
    	 Greeting g =  new Greeting(counter.incrementAndGet(),
                 String.format(template, id));
    	return new ResponseEntity<Greeting>(g, HttpStatus.OK); 
    }
    
    
}
