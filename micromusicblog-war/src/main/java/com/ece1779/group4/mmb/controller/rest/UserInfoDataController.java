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
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ece1779.group4.mmb.model.UserDetail;
import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
/**
 * RESTful api to handle user related information
 * @author chen
 *
 */
@Controller
@RequestMapping("/userData")
public class UserInfoDataController {
	
    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(UserInfo.class.getName());

	@RequestMapping(value="/id/{accountName}",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<UserInfo> getUser(@PathVariable String accountName){
		UserInfo info;
		info = ofy().load().key(Key.create(UserInfo.class,accountName)).now();
		if(info == null){
			return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		}
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

		return new ResponseEntity<List<UserInfo>>(users, HttpStatus.OK);
	}
	
	//User info here only have a simple user profile name and its account Name
	//ofy save will use memcache automatcally
	 @RequestMapping(value="/profile/{profileName}",method = RequestMethod.POST,
	    		headers = {"Content-type=application/json"})
	 @ResponseStatus(value = HttpStatus.CREATED)
	 public void createUserProfile(@PathVariable String profileName){
		 	
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	String accountName = userService.getCurrentUser().getEmail();
		 	UserInfo existUser = ofy().load().key(Key.create(UserInfo.class,accountName)).now();
		 	
		 	if(existUser !=null){
		 		existUser.setProfileName(profileName);
		 		ofy().save().entity(existUser).now();
		 	}
		 	else{
		 		UserInfo userInfo = new UserInfo();
		 		userInfo.setProfileName(profileName);
		 		userInfo.setAccountName(accountName);
		 		userInfo.setFollowerCount(0);
		 		userInfo.setKey(Key.create(UserInfo.class,userService.getCurrentUser().getEmail()));
		 		ofy().save().entity(userInfo).now();
		 	}
	}

	 //following target user
	 @RequestMapping(value="/follow",method = RequestMethod.PUT,
			 headers = {"Content-type=application/json"})
	 public ResponseEntity<UserInfo> followUser(@RequestBody UserDetail targetUser){
		    String accountName = targetUser.getAccountName();
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	String myAccount = userService.getCurrentUser().getEmail();
		 	UserInfo target = ofy().load().key(Key.create(UserInfo.class,accountName)).now();
		 	UserInfo myInfo = ofy().load().key(Key.create(UserInfo.class,myAccount)).now();
		 	
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
		 	
		 	return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT);
	}
	 
	 @RequestMapping(value="/unfollow",method = RequestMethod.PUT,
			 headers = {"Content-type=application/json"})
	 public ResponseEntity<UserInfo> unfollowUser(@RequestBody UserDetail targetUser){
		    String accountName = targetUser.getAccountName();
		 	//this depends on google account service
		 	UserService userService = UserServiceFactory.getUserService();
		 	String myAccount = userService.getCurrentUser().getEmail();
		 	UserInfo target = ofy().load().key(Key.create(UserInfo.class,accountName)).now();
		 	UserInfo myInfo = ofy().load().key(Key.create(UserInfo.class,myAccount)).now();
		 	
		 	
		 	if(target == null){
		 		return new ResponseEntity<UserInfo>(HttpStatus.BAD_REQUEST);
		 	}
		 	
		 	target.decrementFollowerCount();
		 	target.removeFollower(myInfo.getKey());
		 	ofy().save().entity(target);
		 	myInfo.removeFollowing(target.getKey());
		 	ofy().save().entity(myInfo);

		 	return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT);
	}
	 
	 
	 
	 //put existing user in datastore
	 @RequestMapping(value="/{accountName}", method = RequestMethod.PUT,
			 headers = {"Content-type=application/json"})
	 public  ResponseEntity<UserInfo> updateUser(@PathVariable String accountName, @RequestBody UserInfo userInfo){
		 
		ofy().save().entity(userInfo);
		return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT); 
	 }
	 
	 //User info here only have a simple user profile name and its account Name
	 //delete is not used on current stage
	 @RequestMapping(value="/{accountName}", method = RequestMethod.DELETE)
	 public  ResponseEntity<UserInfo> createUser(@PathVariable String accountName){
		ofy().delete().type(UserInfo.class).id(accountName).now();
		return new ResponseEntity<UserInfo>(HttpStatus.NO_CONTENT);
		 
	} 
}
