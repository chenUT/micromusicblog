package com.ece1779.group4.mmb.controller.rest;


import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ece1779.group4.mmb.model.UserDetail;
import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Controller
@RequestMapping("/userData")
public class UserInfoDataController {
	
    private static Logger logger = Logger.getLogger(UserInfo.class.getName());

	@RequestMapping(value="/id/{accountName}",method=RequestMethod.GET)
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
	
	@RequestMapping(value="/name/{profileName}",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<UserInfo>> getUserByName(@PathVariable String profileName){
		UserService userService = UserServiceFactory.getUserService();

		List<UserInfo> users = new ArrayList<UserInfo>();
		Query<UserInfo> usersQ = ofy().load().type(UserInfo.class);
		usersQ = usersQ.filter("profileName ==", profileName);
		users = usersQ.list();
		//users = ofy().load().type(UserInfo.class).list();
		
//		if(users == null){
//			return new ResponseEntity<List<UserInfo>>(HttpStatus.NOT_FOUND);
//		}
//
//		QueryResultIterator<UserInfo> iterator = usersQ.iterator();
//		while(iterator.hasNext()){
//			UserInfo un = iterator.next();
//			users.add(un);
//		}
		 
		UserInfo myAccount=null;
		for(UserInfo u : users){
			if(u.getAccountName().equals(userService.getCurrentUser().getEmail())){
				myAccount = u;
				break;
			}
		}
		if(myAccount != null){
			users.remove(myAccount);
		}
//		if(user == null){
//			user = new UserInfo();
//			user.setProfileName(profileName+" "+c);
//		}
//		users.clear();
//		users.add(myAccount);
		return new ResponseEntity<List<UserInfo>>(users, HttpStatus.OK);
	}
	
	//User info here only have a simple user profile name and its account Name
	 @RequestMapping(value="/profile/{profileName}",method = RequestMethod.POST,
	    		headers = {"Content-type=application/json"})
	 public ResponseEntity<UserInfo> createUserProfile(@PathVariable String profileName){
		 	
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	
		 	UserInfo existUser = ofy().load().type(UserInfo.class).filter("accountName ==",userService.getCurrentUser().getEmail()).first().now();
		 	
		 	if(existUser !=null){
		 		existUser.setProfileName(profileName);
		 		ofy().save().entity(existUser);
		 		return new ResponseEntity<UserInfo>(existUser,HttpStatus.CREATED);
		 	}
		 	else{
		 		UserInfo userInfo = new UserInfo();
		 		userInfo.setProfileName(profileName);
		 		userInfo.setAccountName(userService.getCurrentUser().getEmail());
		 		userInfo.setFollowerCount(0);
		 		userInfo.setKey(Key.create(UserInfo.class,userService.getCurrentUser().getEmail()));
		 		ofy().save().entity(userInfo);
		 		
		 	}
		 	return new ResponseEntity<UserInfo>(HttpStatus.CREATED);
		 	//put userinfo in memcache
//		 	MemcacheService syncCache;
//		 	try{
//		 		syncCache = MemcacheServiceFactory.getMemcacheService();
//		 		syncCache.put(user, arg1);
//		 	}
		 	
		 	
	}
	 
		//User info here only have a simple user profile name and its account Name
	 @RequestMapping(value="/follow",method = RequestMethod.PUT,
			 headers = {"Content-type=application/json"})
	 public ResponseEntity<UserInfo> followUser(@RequestBody UserDetail targetUser){
		    String accountName = targetUser.getAccountName();
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	String myAccount = userService.getCurrentUser().getEmail();
		 	UserInfo target = ofy().load().type(UserInfo.class).filter("accountName ==",accountName).first().now();
		 	UserInfo myInfo = ofy().load().type(UserInfo.class).filter("accountName ==",myAccount).first().now();
		 	
		 	if(target == null){
		 		return new ResponseEntity<UserInfo>(HttpStatus.BAD_REQUEST);
		 	}
		 	
		 	target.incrementFollowerCount();
		 	System.out.println(accountName+"adding follower with key "+myInfo.getKey().getString()+" from "+myAccount);
		 	target.addFollower(myInfo.getKey());
		 	ofy().save().entity(target).now();
		 	
		 	System.out.println("adding following with key "+target.getKey().getString());
		 	Key<UserInfo> anotherKey = Key.create(UserInfo.class,target.getAccountName());
		 	
		 	myInfo.addFollowing(anotherKey);
		 	System.out.println("i am following :"+myInfo.getFollowings().size()+" people now");
		 	ofy().save().entity(myInfo).now();
		 	
		 	UserInfo myInfoText = ofy().load().type(UserInfo.class).filter("accountName ==",myAccount).first().now();
			System.out.println("i am following :"+myInfoText.getFollowings().size()+" people now (retrive data from datastore)");
		 	//put userinfo in memcache
//		 	MemcacheService syncCache;
//		 	try{
//		 		syncCache = MemcacheServiceFactory.getMemcacheService();
//		 		syncCache.put(user, arg1);
//		 	}
		 	
		 	return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT);
	}
	 
	 @RequestMapping(value="/unfollow",method = RequestMethod.PUT,
			 headers = {"Content-type=application/json"})
	 public ResponseEntity<UserInfo> unfollowUser(@RequestBody UserDetail targetUser){
		    String accountName = targetUser.getAccountName();
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	String myAccount = userService.getCurrentUser().getEmail();
		 	UserInfo target = ofy().load().type(UserInfo.class).filter("accountName ==",accountName).first().now();
		 	
		 	UserInfo myInfo = ofy().load().type(UserInfo.class).filter("accountName ==",myAccount).first().now();
		 	
		 	
		 	if(target == null){
		 		return new ResponseEntity<UserInfo>(HttpStatus.BAD_REQUEST);
		 	}
		 	
		 	target.decrementFollowerCount();
		 	target.removeFollower(myInfo.getKey());
		 	ofy().save().entity(target);
		 	myInfo.removeFollowing(target.getKey());
		 	ofy().save().entity(myInfo);
		 	
		 	//put userinfo in memcache
//		 	MemcacheService syncCache;
//		 	try{
//		 		syncCache = MemcacheServiceFactory.getMemcacheService();
//		 		syncCache.put(user, arg1);
//		 	}
		 	
		 	return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT);
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
