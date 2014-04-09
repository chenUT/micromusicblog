package com.ece1779.group4.mmb.controller.mvc;	

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ece1779.group4.mmb.model.Greeting;
import com.ece1779.group4.mmb.model.Post;
import com.ece1779.group4.mmb.model.PostInfo;
import com.ece1779.group4.mmb.model.SimplePostObj;
import com.ece1779.group4.mmb.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView welcome(ModelMap model) {
		
		 UserService userService = UserServiceFactory.getUserService(); 
		 String accountName = userService.getCurrentUser().getEmail();
		 System.out.println("account name: "+accountName);
		 UserInfo user = ofy().load().type(UserInfo.class).filter("accountName ==",userService.getCurrentUser().getEmail()).first().now();
	     
         if(user == null){
	     System.out.println("userinfo null");
        	 ModelAndView regModel = new ModelAndView("register");
       	 	return regModel;
         }

		 //set up token for channel api
		 //for simplicity reason we use user key as token
		 //in real life a hased key should be used instead
		 ChannelService channelService = ChannelServiceFactory.getChannelService();
		 String token = channelService.createChannel(user.getKey().getString());
		 model.addAttribute("token", token);

		 model.addAttribute("userProfileName", user.getProfileName());
		 model.addAttribute("logouturl",userService.createLogoutURL("/login")); 
	
		 List<PostInfo> postInfoList = new ArrayList<PostInfo>();
		 int bufferIndex=0;
		 PostInfo tempPostInfo;
		 
		 
		 Map<Key<Post>,Post> myPostMap =ofy().load().keys(user.getPosts());
		 List<Post> postsFromMe = new ArrayList<Post>(myPostMap.values());
		 System.out.println("postsFromMe: "+postsFromMe.size());
		 for(int i=0; i<postsFromMe.size();i++){
			 Post currPost = postsFromMe.get(i);
			 tempPostInfo = new PostInfo();
			 if(currPost.getComment() == null){
				 tempPostInfo.setComment("No Comment");
			 }
			 else{
				 tempPostInfo.setComment(currPost.getComment());
			 }
			 tempPostInfo.setCreater(user.getProfileName());
			 tempPostInfo.setPostKey(currPost.getKey().getString());
			 tempPostInfo.setCreateTime(currPost.getCreatedTime());
			 postInfoList.add(tempPostInfo);
		 }
		 ModelAndView profModel = new ModelAndView("profile");

		 
		 //find all top 200 posts
		 
		 //create a buffer for 200 simple posts
//		 PostInfo[] postInfoBuffer = new PostInfo[200]; 
		 PostInfo[] postInfoBuffer = new PostInfo[20]; 
		
		 System.out.println("i am following: "+user.getFollowings().size());
		 if(user.getFollowings().size() !=0){
			 UserInfo t = ofy().load().key(user.getFollowings().get(0)).now();
			if(t != null){
				 System.out.println("i am following: "+t.getProfileName());
			}
			else{
				 System.out.println("Error!!");
			}
			 
			 Map<Key<UserInfo>,UserInfo> followingMap =ofy().load().keys(user.getFollowings());
			 if(followingMap != null){
				 List<UserInfo> infos = new ArrayList<UserInfo>(followingMap.values());
				 System.out.println("i am following: "+infos.size());
				 //Here we are going through all post from the users we are following
				 //we only get 20 for demo purpose
				 //TODO find the latest 200 post and return 20 of them
				 //we store the rest in memcache for later request
				 //TODO a more efficient searching algorithm is preferred
//				 long earliesPostTime=Long.MAX_VALUE; //timestamp for earliest post
//				 Post earliestPost;
//				 int earliestPostIndex=0;
				 for(UserInfo info : infos){
					 Map<Key<Post>,Post> postMap =ofy().load().keys(info.getPosts());
					 
					 System.out.println("postMap size: "+postMap.size());
					 List<Post> postsForUser = new ArrayList<Post>(postMap.values());
					 for(int i=0; i<postsForUser.size();i++){
						 Post currPost = postsForUser.get(i);
						 tempPostInfo = new PostInfo();
						 if(currPost.getComment() == null){
							 tempPostInfo.setComment("No Comment");
						 }
						 else{
							 tempPostInfo.setComment(currPost.getComment());
						 }
						 tempPostInfo.setCreater(info.getProfileName());
						 tempPostInfo.setPostKey(currPost.getKey().getString());
						 tempPostInfo.setCreateTime(currPost.getCreatedTime());
						 postInfoList.add(tempPostInfo);
						 //compare each post with earlies post time
//						 if(currPost.getCreatedTime()<earliesPostTime){
//							 earliestPost = postsForUser.get(i);
//							 earliesPostTime = earliestPost.getCreatedTime();
//							 if(bufferIndex < postInfoBuffer.length){
//								tempPostInfo = new PostInfo();
//								if(currPost.getComment() == null){
//                                    tempPostInfo.setComment("No Comment");
//								}
//								else{
//									tempPostInfo.setComment(currPost.getComment());
//								}
//								tempPostInfo.setCreater(info.getProfileName());
//								tempPostInfo.setPostKey(currPost.getKey().toString());
//								tempPostInfo.setCreateTime(currPost.getCreatedTime());
//								postInfoBuffer[bufferIndex] = tempPostInfo;
//								earliestPostIndex =bufferIndex; 
//								bufferIndex++;
//							 }
//						 }
//						 else{//curr is later post, remove older post then find the oldest post againg
//							 tempPostInfo = new PostInfo();
//								if(currPost.getComment() == null){
//                                 tempPostInfo.setComment("No Comment");
//								}
//								else{
//									tempPostInfo.setComment(currPost.getComment());
//								}
//								tempPostInfo.setCreater(info.getProfileName());
//								tempPostInfo.setPostKey(currPost.getKey().toString());
//								tempPostInfo.setCreateTime(currPost.getCreatedTime());
//							 if(bufferIndex < postInfoBuffer.length){
//								postInfoBuffer[bufferIndex] = tempPostInfo;
//								bufferIndex++;
//							 }
//							 else{
//								 postInfoBuffer[earliestPostIndex] = tempPostInfo;
//								 earliestPostIndex = searchEarliestIndex(postInfoBuffer);
//								 earliesPostTime = postInfoBuffer[earliestPostIndex].getCreateTime();
//							 }
//						 }
					 }
				 }
				 
				 
			 }
		 }
		 
		 profModel.addObject("postInfos",postInfoList);
//		 List<String> t = getList();
		 //profModel.addObject(attributeName, attributeValue)
         
		 //check whether user is a registered user
       	 return profModel;
	}
	
	private class PostInfoComparator implements Comparator<PostInfo>{
		@Override
		public int compare(PostInfo o1, PostInfo o2) {
			if((o1.getCreateTime()-o2.getCreateTime())>0){
				return -1;
			}
			return 1;
		}
	}
	
	private int searchEarliestIndex(PostInfo[] posts){
		int index=0;
		long earliestTime = Long.MAX_VALUE;
		
		//use quick search instead
		for(int i=0;i<posts.length ;i++){
			if(posts[i].getCreateTime() < earliestTime){
				index = i;
				earliestTime = posts[i].getCreateTime();
			}
		}
		
		return index;
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
	
	@RequestMapping(method = RequestMethod.POST,
    		headers = {"Content-type=application/json"})
	public void postInfo(){
		Greeting g = new Greeting();
		g.setContent("test");
		g.setId(123l);
		
		UserService userService = UserServiceFactory.getUserService(); 
		String accountName = userService.getCurrentUser().getEmail();
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String channelKey = accountName;
	
		
		
		ObjectWriter mapper = new ObjectMapper().writer();
		String json="{}";
		try {
			json = mapper.writeValueAsString(g);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		  
		channelService.sendMessage(new ChannelMessage(channelKey,json));
		
		//return new ResponseEntity<Greeting>(g,HttpStatus.OK);
	}
	
}
