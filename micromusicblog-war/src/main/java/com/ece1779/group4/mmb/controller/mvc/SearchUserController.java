package com.ece1779.group4.mmb.controller.mvc;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ece1779.group4.mmb.model.UserDetail;
import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Controller
@RequestMapping(value="/searchuser")
public class SearchUserController {
	
	@RequestMapping(value="/{profileName}", method = RequestMethod.GET)
	public ModelAndView welcome(@PathVariable String profileName, ModelMap model) {
		
		UserService userService = UserServiceFactory.getUserService();
		String myAccountId = userService.getCurrentUser().getEmail();
		
		ModelAndView searchResultMAV = new ModelAndView("searchuser");
		

		List<UserInfo> users = new ArrayList<UserInfo>();
		Query<UserInfo> usersQ = ofy().load().type(UserInfo.class);
		usersQ = usersQ.filter("profileName ==", profileName);
		users = usersQ.list();

		UserInfo myInfo = ofy().load().type(UserInfo.class).filter("accountName ==",myAccountId).first().now();
	     
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
		 
		 Map<Key<UserInfo>,UserInfo> followingMap=null;
		 if(myInfo.getFollowings()!=null){
			 if(myInfo.getFollowings().size() !=0){
				 followingMap =ofy().load().keys(myInfo.getFollowings());
			 }
		 }
		
		List<UserDetail> details = new ArrayList<UserDetail>();
		UserDetail resultUser;
		for(UserInfo u : users){
			if(!u.getAccountName().equals(myAccountId)){
				resultUser = new UserDetail();
				resultUser.setAccountName(u.getAccountName());
				resultUser.setProfileName(u.getProfileName());
			
                if(followingMap != null){
                     if(followingMap.containsKey(u.getKey())){
                    	 resultUser.setIsFollowing(true);
                     }
                     else{
                    	 resultUser.setIsFollowing(false);
                     }
                }
                else{
                	resultUser.setIsFollowing(false);
                }
                details.add(resultUser);
			}
		}
//		if(myAccount != null){
//			users.remove(myAccount);
//		}
		
		System.out.println("users count "+users.size());
		 List<String> t = getList();
		 searchResultMAV.addObject("lists",details);
		return searchResultMAV;
		
	}
	private List<String> getList() {
		 
		List<String> list = new ArrayList<String>();
		list.add("List A");
		list.add("List B");
		list.add("List C");
		list.add("List D");
		list.add("List 1");
		list.add("List 2");
		list.add("List 3");
 
		return list;
 
	}
	

}
