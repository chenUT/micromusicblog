package com.ece1779.group4.mmb.controller.rest;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ece1779.group4.mmb.model.Post;

@Controller
public class PostDataController {
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Post> getUser(@PathVariable String id){
		
		//TODO we need refactor this so it returns a url rather than post binary data
	   Post post;
	   post = ofy().load().type(Post.class).id(id).now();
		   if(post == null){
			   return new ResponseEntity<Post>(HttpStatus.NOT_FOUND);
		   }
		   
		return new ResponseEntity<Post>(post, HttpStatus.OK);
	}
	
	//User info here only have a simple user profile name and its account Name
		 @RequestMapping(method = RequestMethod.POST,
		    		headers = {"Content-type=application/json"})
		 public ResponseEntity<Post> createPost(@RequestBody Post post){
			 	
			 	
			 	if(ofy().load().type(Post.class).filter("id", post.getId()).count() != 0){
			 		return new ResponseEntity<Post>(HttpStatus.CONFLICT);
			 	}
			 	ofy().save().entity(post);
		
			 	//put Post in memcache
//			 	MemcacheService syncCache;
//			 	try{
//			 		syncCache = MemcacheServiceFactory.getMemcacheService();
//			 		syncCache.put(user, arg1);
//			 	}
			 	
			 	return new ResponseEntity<Post>(post,HttpStatus.CREATED);
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
