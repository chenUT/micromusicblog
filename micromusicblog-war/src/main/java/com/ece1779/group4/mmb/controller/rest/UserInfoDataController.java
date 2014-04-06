package com.ece1779.group4.mmb.controller.rest;


import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ece1779.group4.mmb.model.Post;
import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

@Controller
@RequestMapping("/userData")
public class UserInfoDataController {
	
    private static Logger logger = Logger.getLogger(UserInfo.class.getName());

	@RequestMapping(value="/{accountName}",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<UserInfo> getUser(@PathVariable String accountName){
		
	   UserInfo info;
//	   MemcacheService syncCache=null;
//	   try {
//		  syncCache = MemcacheServiceFactory.getMemcacheService();
//		   syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		   //info = (UserInfo)syncCache.get(accountName);
//	   } catch (MemcacheServiceException e) {
//           // If there is a problem with the cache,
//           // fall through to the datastore.
//		   info = null;
//	   }
	   //cache miss
	  // if(info == null){
		   //get userinfo from datastore
	
	    	//info using memcache automatically by objectify framework
		   info = ofy().load().type(UserInfo.class).id(accountName).now();
		   if(info == null){
			   return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		   }
		   
		   //store user info to memcache
//		   if(syncCache != null){
//			   syncCache.put(accountName, info);
//		   }
	   //}
//		ofy().
		return new ResponseEntity<UserInfo>(info, HttpStatus.OK);
	}
	
	//User info here only have a simple user profile name and its account Name
	 @RequestMapping(method = RequestMethod.POST,
	    		headers = {"Content-type=application/json"})
	 public ResponseEntity<UserInfo> createUser(@RequestBody UserInfo userInfo){
		 	
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	userInfo.setAccountName(userService.getCurrentUser().getEmail());
		 	
		 	
		 	userInfo.setFollowerCount(0);
		 	userInfo.setPosts(new ArrayList<Key<Post>>());
		 	userInfo.setFlollowings(new ArrayList<Key<UserInfo>>());
		 	
		 	if(ofy().load().type(UserInfo.class).filter("accountName", userInfo.getAccountName()).count() != 0){
		 		return new ResponseEntity<UserInfo>(HttpStatus.CONFLICT);
		 	}
		 	ofy().save().entity(userInfo);
		 	
		 	//put userinfo in memcache
//		 	MemcacheService syncCache;
//		 	try{
//		 		syncCache = MemcacheServiceFactory.getMemcacheService();
//		 		syncCache.put(user, arg1);
//		 	}
		 	
		 	return new ResponseEntity<UserInfo>(userInfo,HttpStatus.CREATED);
	}
	 
	 @RequestMapping(value="/{accountName}", method = RequestMethod.PUT,
			 headers = {"Content-type=application/json"})
	 public  ResponseEntity<UserInfo> updateUser(@PathVariable String accountName, @RequestBody UserInfo userInfo){
		 ofy().save().entity(userInfo);
//		 UserInfo newInfo = ofy().load().type(UserInfo.class).id(accountName).now();
		return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT); 
	 }
	 
	//User info here only have a simple user profile name and its account Name
	 @RequestMapping(value="/{accountName}", method = RequestMethod.DELETE)
	 public  ResponseEntity<UserInfo> createUser(@PathVariable String accountName){
		 //first remove from memcache
		// MemcacheService syncCache=null;
//		 boolean deleted = ;
//		 try {
//			  syncCache = MemcacheServiceFactory.getMemcacheService();
//			  if(syncCache.contains(accountName)){
//				deleted = syncCache.delete(accountName);
//				if(deleted){
					ofy().delete().type(UserInfo.class).id(accountName).now();
//				}
			//  }
//		   } catch (MemcacheServiceException e) {
//	           // If there is a problem with the cache,
//	           // fall through to the datastore.
//		   }
//		 
//		 if(deleted){
			 return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT);
//		 }
//		 else{
//			 return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
//		 }
		 
	} 
}
