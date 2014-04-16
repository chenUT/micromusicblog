package com.ece1779.group4.mmb.controller.mvc;	

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ece1779.group4.mmb.model.PostInfo;
import com.ece1779.group4.mmb.model.PostMeta;
import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView welcome(ModelMap model) {
		
		 UserService userService = UserServiceFactory.getUserService(); 
		 String accountName = userService.getCurrentUser().getEmail();
		 System.out.println("account name: "+accountName);
		 UserInfo user = ofy().load().key(Key.create(UserInfo.class,accountName)).now();
	     
         if(user == null){
        	System.out.println("userinfo not in datastore");
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
		 PostInfo tempPostInfo;
		 Map<Key<PostMeta>,PostMeta> myPostMap =ofy().load().keys(user.getPosts());
		 Set<Key<PostMeta>>keys = myPostMap.keySet();
		 Iterator<Key<PostMeta>> iter = keys.iterator();
		 
		 System.out.println("postsFromMe: "+myPostMap.size());
		 model.addAttribute("postCount",myPostMap.size());
		 
//		 for(int i=0; i<postsFromMe.size();i++){
		 Key<PostMeta> tempKey=null;
		 PostMeta currPost;
		 while(iter.hasNext()){
			 tempKey = iter.next(); 
		 	 currPost = myPostMap.get(tempKey);
//		 	 System.out.println("postsFromMe: "+currPost.getFormat());
//			 Post currPost = postsFromMe.get(i);
			 tempPostInfo = new PostInfo();
			 if(currPost !=null){
				 if(currPost.getBackgroundPostMetaKey() == null){
					 tempPostInfo.setComment("Original");
				 }
				 else{
					 tempPostInfo.setComment("Mixed");
				 }
				 tempPostInfo.setCreater(user.getProfileName());
				 tempPostInfo.setPostKey(currPost.getKey().getString());
				 tempPostInfo.setCreateTime(currPost.getCreatedTime());
				 postInfoList.add(tempPostInfo);
			 }
		 }
		 ModelAndView profModel = new ModelAndView("profile");

		 
		 //find all top 200 posts
		 
		 //create a buffer for 200 simple posts
//		 PostInfo[] postInfoBuffer = new PostInfo[200]; 
		
		 System.out.println("i am following: "+user.getFollowings().size());
		 model.addAttribute("followingCount",user.getFollowings().size());
		 model.addAttribute("followerCount",user.getFollowers().size());
		 
		 
		 if(user.getFollowings().size() !=0){
			 String keyString = user.getFollowings().get(0).getString();
			 System.out.println("i am following: "+keyString);
			 Key<UserInfo> tk =Key.create(keyString);
			 UserInfo t = ofy().load().key(tk).now();
			if(t != null){
				 System.out.println("i am following: "+t.getProfileName());
			}
			else{
				 System.out.println("Error!!");
			}
			 
			 Map<Key<UserInfo>,UserInfo> followingMap =ofy().load().keys(user.getFollowings());
			 if(followingMap != null){
                 Set<Key<UserInfo>> followingUserInfoKeys = followingMap.keySet();
				 //List<UserInfo> infos = new ArrayList<UserInfo>(followingMap.values());
				 System.out.println("i am following: "+followingMap.size());
				 //Here we are going through all post from the users we are following
				 //we only get 20 for demo purpose
				 //TODO find the latest 200 post and return 20 of them
				 //we store the rest in memcache for later request
				 //TODO a more efficient searching algorithm is preferred
				 Iterator<Key<UserInfo>> infoKeyIter = followingUserInfoKeys.iterator();
				 UserInfo info;
				 Key<UserInfo> currKey;
				 while(infoKeyIter.hasNext()){
					 currKey = infoKeyIter.next();
					 info = followingMap.get(currKey);
					 List<PostMeta> postsForUser = new ArrayList<PostMeta>();
					 //first all posts are loaded
					 for(Key<PostMeta> pk : info.getPosts()){
						 System.out.println("following post with key "+pk.getString());
						 PostMeta p = ofy().load().key(pk).now();
						 if(p!=null){
							 postsForUser.add(p);
						 }
					 }
					 //sync load					
					 for(int i=0; i<postsForUser.size();i++){
						 currPost = postsForUser.get(i);
						 tempPostInfo = new PostInfo();
						 if(currPost !=null){
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
						 }
						 //compare each post with earlies post time
					 }
				 }
			 }
		 }
		 System.out.println("total posts in list: "+postInfoList.size());
		 Collections.sort(postInfoList,new PostInfoComparator());
		 profModel.addObject("postInfos",postInfoList);
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
	

//	@RequestMapping(method = RequestMethod.POST,
//    		headers = {"Content-type=application/json"})
//	public void postInfo(){
//	}
}
