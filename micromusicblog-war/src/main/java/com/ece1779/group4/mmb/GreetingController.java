package com.ece1779.group4.mmb;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ece1779.group4.mmb.model.Greeting;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

@Controller   
@RequestMapping("/greeting")
public class GreetingController {

    private static final String template = "Hello, %s!";
    //private final AtomicLong counter = new AtomicLong();
//    static{
//		ObjectifyService.register(Greeting.class);
//	}
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Greeting> getGreetingAll(){
//    	 Greeting g =  new Greeting(String.format(template, "world"));
//    	 List<Greeting> result = new ArrayList<Greeting>(); 
//    	 result.add(g);
    	return ofy().load().type(Greeting.class).list();
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public  ResponseEntity<Greeting> getGreeting( @PathVariable String id) {
//      return new Greeting(counter.incrementAndGet(),
//      String.format(template, name));
    	 Greeting g =  new Greeting(String.format(template, id));
    	return new ResponseEntity<Greeting>(g, HttpStatus.OK); 
    }
    
    @RequestMapping(method = RequestMethod.POST,
    		headers = {"Content-type=application/json"})
    public ResponseEntity<Greeting> createGreeting(@RequestBody Greeting g){
    	Key<Greeting> resultKey = ofy().save().entity(g).now();
    	Greeting  result = ofy().load().key(resultKey).now();
    
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	return new ResponseEntity<Greeting>(result, headers, HttpStatus.CREATED);
    }
}
