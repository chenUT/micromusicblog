package com.ece1779.group4.mmb.controller.rest;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ece1779.group4.mmb.model.Post;
import com.ece1779.group4.mmb.model.PostData;
import com.ece1779.group4.mmb.model.PostInfo;
import com.ece1779.group4.mmb.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

@RequestMapping(value="/postData")
@Controller
public class PostDataController {
	@RequestMapping(value="/{postKeyString}",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<PostData> getPost(@PathVariable String postKeyString){
		
		//TODO we need refactor this so it returns a url rather than post binary data
	   Post post;
	   
	   Key<Post> tempKey = Key.create(postKeyString);
	   
	   post = ofy().load().key(tempKey).now();
	  
	   if(post == null){
		   return new ResponseEntity<PostData>(HttpStatus.NOT_FOUND);
	   }
		   
		PostData postData = new PostData();
		postData.setData(post.getData());
		postData.setFormat(post.getFormat());
	
		//clear memory
		post = null;
		
		return new ResponseEntity<PostData>(postData, HttpStatus.OK);
	}
	
//	@RequestMapping(value="/user",method=RequestMethod.GET)
//	@ResponseBody
//	public ResponseEntity<Post> getPostInfoForUser(){
//		
//		  UserService userService = UserServiceFactory.getUserService();
//		  String myAccountName = userService.getCurrentUser().getEmail();
//		  
//		  
//		
//		return null;
//	}
	
	//User info here only have a simple user profile name and its account Name
		 @RequestMapping(method = RequestMethod.POST)
		 public ResponseEntity<PostInfo> createPost(HttpServletRequest req, HttpServletResponse res)
			      throws ServletException, IOException {
			 	
			 try {
			      ServletFileUpload upload = new ServletFileUpload();

			      FileItemIterator iterator = upload.getItemIterator(req);
			      while (iterator.hasNext()) {
			        FileItemStream item = iterator.next();
			        InputStream stream = item.openStream();

			        if (item.isFormField()) {
			          System.out.println("Got a form field: " + item.getFieldName()+" field value: "+item.getName());
			        } else {
			        	 System.out.println("Got an uploaded file: " + item.getFieldName() +
			                      ", name = " + item.getName());

			          // You now have the filename (item.getName() and the
			          // contents (which you can read from stream). Here we just
			          // print them back out to the servlet output stream, but you
			          // will probably want to do something more interesting (for
			          // example, wrap them in a Blob and commit them to the
			          // datastore).
			          int len;
			          byte[] buffer = new byte[8192];

			          ByteArrayOutputStream theFile = new ByteArrayOutputStream();
			          
			          while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
			        	theFile.write(buffer, 0, len);  
			          }
			          Blob b =new Blob(theFile.toByteArray());
			          theFile.close();
			          System.out.println("blob size: "+b.getBytes().length);
			          Post post = new Post();
			          post.setId(""+System.currentTimeMillis());
			          post.setData(b.getBytes());
			          post.setCreatedTime(System.currentTimeMillis());
			          post.setKey(Key.create(Post.class,post.getId()));
			          Key<Post>postKey = post.getKey();
			          ofy().save().entity(post);
			          
			          //find current user and add it to its post list
			        //this depends on google account service
					  UserService userService = UserServiceFactory.getUserService();
			          UserInfo poster =  ofy().load().type(UserInfo.class).filter("accountName ==",userService.getCurrentUser().getEmail()).first().now();
//					  add post to poster
			          poster.addPost(postKey);
			          System.out.println("post added for "+poster.getAccountName());
			          ofy().save().entity(poster);
			          
			          PostInfo newInfo = new PostInfo();
//			          newInfo.
			          newInfo.setCreater(poster.getProfileName()); 
			          newInfo.setPostKey(postKey.getString());
			          newInfo.setCreateTime(post.getCreatedTime());
			          
			          //notify all followers
			  		ChannelService channelService = ChannelServiceFactory.getChannelService();
			  		List<Key<UserInfo>> followers = poster.getFollowers();
			  		String channelKey="";
			  		
			  		//create new post to json
			  		ObjectWriter mapper = new ObjectMapper().writer();
					String json="{}";
					try {
						json = mapper.writeValueAsString(newInfo);
						for(int i=0;i<followers.size();i++){
				  			channelKey = followers.get(i).getString();
				  			channelService.sendMessage(new ChannelMessage(channelKey,json));
				  		}
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//			          d.setData(b.getBytes());
			          return new ResponseEntity<PostInfo>(newInfo, HttpStatus.OK);
			        }
			      }
			    } catch (Exception ex) {
			    	System.out.println(ex.getMessage());
			      throw new ServletException(ex);
			    }
			 
			 return null;
			 	
//			 	if(ofy().load().type(Post.class).filter("id", post.getId()).count() != 0){
//			 		return new ResponseEntity<Post>(HttpStatus.CONFLICT);
//			 	}
//			 	ofy().save().entity(post);
//		
//			 	//put Post in memcache
////			 	MemcacheService syncCache;
////			 	try{
////			 		syncCache = MemcacheServiceFactory.getMemcacheService();
////			 		syncCache.put(user, arg1);
////			 	}
//			 	
//			 	return new ResponseEntity<Post>(post,HttpStatus.CREATED);
		}
		 
		 @RequestMapping(value="/{postId}", method = RequestMethod.PUT,
				 headers = {"Content-type=application/json"})
		 public  ResponseEntity<Post> updatePost(@PathVariable String postId, @RequestBody Post post){
			 ofy().save().entity(post);
//			 Post newInfo = ofy().load().type(Post.class).id(accountName).now();
			return new ResponseEntity<Post>(HttpStatus.NO_CONTENT); 
		 }
		 
		//User info here only have a simple user profile name and its account Name
		 @RequestMapping(value="/{postId}", method = RequestMethod.DELETE)
		 public  ResponseEntity<Post> createPost(@PathVariable String postId){
			 //first remove from memcache
			// MemcacheService syncCache=null;
//			 boolean deleted = ;
//			 try {
//				  syncCache = MemcacheServiceFactory.getMemcacheService();
//				  if(syncCache.contains(accountName)){
//					deleted = syncCache.delete(accountName);
//					if(deleted){
						ofy().delete().type(Post.class).id(postId).now();
//					}
				//  }
//			   } catch (MemcacheServiceException e) {
//		           // If there is a problem with the cache,
//		           // fall through to the datastore.
//			   }
//			 
//			 if(deleted){
				 return new ResponseEntity<Post>(HttpStatus.NO_CONTENT);
//			 }
//			 else{
//				 return new ResponseEntity<Post>(HttpStatus.NOT_FOUND);
//			 }
			 
		} 
	
}
