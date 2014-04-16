package com.ece1779.group4.mmb.controller.rest;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ece1779.group4.mmb.model.PostData;
import com.ece1779.group4.mmb.model.PostDataReturn;
import com.ece1779.group4.mmb.model.PostInfo;
import com.ece1779.group4.mmb.model.PostMeta;
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

@RequestMapping(value="/postData")
@Controller
public class PostDataController {
	@RequestMapping(value="/{postKeyString}",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<PostDataReturn> getPost(@PathVariable String postKeyString){
		
		//TODO we need refactor this so it returns a url rather than post binary data
	   PostMeta post;
	   
	   Key<PostMeta> tempKey = Key.create(postKeyString);
	   
	   post = ofy().load().key(tempKey).now();
	  
	   if(post == null){
		   return new ResponseEntity<PostDataReturn>(HttpStatus.NOT_FOUND);
	   }
		   
	   PostDataReturn returnData = new PostDataReturn();
	   //reconstruct 
	   byte[] originalData = null;
	   
	   int chunkCount = post.getDataMap().size();
	   Key<PostData> postDataKey;
	   PostData tempData;
	   for(int i =0;i<chunkCount ; i++){
		   postDataKey = post.getPostDataKey(i);
		   tempData = ofy().load().key(postDataKey).now();
		   if(i == 0){
			  originalData = tempData.getData(); 
		   }
		   else{
			   originalData = concat(originalData, tempData.getData());
		   }
	   }
	   System.out.println("data size"+ originalData.length);
	   
	   byte[][] resultT = new byte[1][];
	   String[] formats = new String[1];
	   Key<PostMeta> backgroundMeta = post.getBackgroundPostMetaKey();
	   if(backgroundMeta != null){
		   PostMeta back =  ofy().load().key(backgroundMeta).now();
		   if(back !=null){
			   chunkCount = back.getDataMap().size();
			   byte[] originalBack = null;
			   for(int i =0;i<chunkCount ; i++){
				   postDataKey = back.getPostDataKey(i);
				   tempData = ofy().load().key(postDataKey).now();
				   if(i == 0){
					   originalBack = tempData.getData(); 
				   }
				   else{
					   originalBack = concat(originalBack, tempData.getData());
				   }
			   }
			   resultT = new byte[2][];
			   resultT[1] = originalBack;
			   formats = new String[2];
			   formats[1] = back.getFormat();
		   }
	   }
	   resultT[0] = originalData;
	   returnData.setData(resultT);
	   formats[0] = post.getFormat();
	   returnData.setFormat(formats);
	   //clear memory
	   post = null;

	   return new ResponseEntity<PostDataReturn>(returnData, HttpStatus.OK);
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
		 @RequestMapping(value="/{mixKey}",method = RequestMethod.POST)
		 public ResponseEntity<PostInfo> createPost(@PathVariable String mixKey, HttpServletRequest req, HttpServletResponse res)
			      throws ServletException, IOException {
			 
			 try {
			      ServletFileUpload upload = new ServletFileUpload();

			      FileItemIterator iterator = upload.getItemIterator(req);
			      while (iterator.hasNext()) {
			        FileItemStream item = iterator.next();
			        InputStream stream = item.openStream();

			        if (item.isFormField()) {
			          System.out.println("Got a form field: " + item.getFieldName()+" field value: "+item.toString()+" "+item.getContentType()+" "+item.getHeaders());
			        } else {
			        	 System.out.println("Got an uploaded file: " + item.getFieldName() +
			                      ", name = " + item.getName());
			        	 
			        	 //we store the audio format as the blob field name
			        	 String audioFormat = item.getFieldName();

			        	 //read the blob file from filestream
			        	 int len;
			        	 byte[] buffer = new byte[8192];
			        	 ByteArrayOutputStream theFile = new ByteArrayOutputStream();

			        	 while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
			        		 theFile.write(buffer, 0, len);  
			        	 }
			        	 //store data in entity
			        	 PostMeta postMeta = new PostMeta();
			        	 //we split the data into chunks of 768kb of data

			        	 List<PostData> dataChunkList = new ArrayList<PostData>();
			        	 int chunkSize= 1024*768;
			        	 byte[] postDataTotal = theFile.toByteArray();
			        	 theFile.close();
			        	 System.out.println("blob size: "+postDataTotal.length);
			        	 PostData dataChunk;
			        	 int dataLength = postDataTotal.length;
			        	 double chunkRatio = (double)postDataTotal.length/chunkSize; 
			        	 int chunkCount;
			        	 if(chunkRatio%1 != 0){
			        		 chunkCount = (int)chunkRatio+1;
			        	 }
			        	 else{
			        		 //this is rare case
			        		 chunkCount = (int)chunkRatio;
			        	 }

			        	 int begin;
			        	 int end;
//			        	 byte[] dataChunkByteArray;
			        	 for(int i=0; i<chunkCount; i++){
			        		 begin = i*chunkSize;
			        		 end= (i+1)*chunkSize;
			        		 dataChunk = new PostData();
//			        		 if(i != chunkCount -1){
//			        			 dataChunkByteArray = new byte[chunkSize];
//			        		 }
			        		 if(i == chunkCount -1 ){//last chunk may have a different size
			        			 int leftOverSize = dataLength - chunkSize*i;
			        			 end = begin + leftOverSize; //change end to new position
			        		 }
			        		 //copy data to chunk
			        		 dataChunk.setData(Arrays.copyOfRange(postDataTotal, begin, end));
			        		 dataChunk.setPostId(System.currentTimeMillis()+(long)i+(long)(Math.random()*100.0));
			        		 dataChunkList.add(dataChunk);

			        		 //set the datachunk index and key to postMeta
			        		 postMeta.addPostData(dataChunk.getKey(),i);
			        	 }
			        	 
			        	 ofy().save().entities(dataChunkList).now();
			        	 
			        	 postMeta.setFormat(audioFormat);
			        	 if(!mixKey.equals("new")){
			        		 Key<PostMeta> temp = Key.create(mixKey);
			        	 }
			        	 postMeta.setBackgroundPostMetaKey(temp);
//			        	 Blob b =new Blob(theFile.toByteArray());
			        	 //blob retrieved
			        	 //get current user infomation
                         UserService userService = UserServiceFactory.getUserService();
			        	 String userAccount = userService.getCurrentUser().getEmail();
                         
			        	 long createdTime = System.currentTimeMillis();
			        	 
			        	 postMeta.setId(userAccount+"_"+createdTime); //we use current time stamp as the post id
			        	 postMeta.setCreatedTime(createdTime);
			        	 
			        	 //store post in datastore

			        	 //find current user and add it to its post list
			        	 //this depends on google account service
			        	 UserInfo poster =  ofy().load().type(UserInfo.class).filter("accountName ==",userAccount).first().now();
			        	 //					  add post to poster
			        	 poster.addPost(postMeta.getKey());
                         System.out.println("post added to poster");
                         System.out.println("post added for "+poster.getAccountName());
                         System.out.println("post added with id "+postMeta.getKey().getString());
			        	 
			        	 ofy().save().entity(poster).now();
			        	 ofy().save().entity(postMeta).now();
			        	 
			        	 //create return postInfo to client
			        	 PostInfo newPostInfo = new PostInfo();
			        	 newPostInfo.setCreater(poster.getProfileName()); 
			        	 newPostInfo.setPostKey(postMeta.getKey().getString());
			        	 newPostInfo.setCreateTime(postMeta.getCreatedTime());
			        	 newPostInfo.setOwnerKey(poster.getKey().getString());
			        	 newPostInfo.setComment(postMeta.getComment());

			        	 //notify all followers
                         System.out.println("creating channel to followers");
			        	 ChannelService channelService = ChannelServiceFactory.getChannelService();
			        	 String channelKey="";
			        	 //post to the post user first
			        	
			        	 
			        	 List<Key<UserInfo>> followers = poster.getFollowers();
			        	 System.out.println("total followers count: "+followers.size());
			        	 //create new post to json
			        	 ObjectWriter mapper = new ObjectMapper().writer();
			        	 String json="{}";
			        	 try {
			        		 json = mapper.writeValueAsString(newPostInfo);
			        		 channelKey = poster.getKey().getString();
			        		 channelService.sendMessage(new ChannelMessage(channelKey,json));//post back first
			        		 for(int i=0;i<followers.size();i++){
			        			 System.out.println("sending notification");
			        			 channelKey = followers.get(i).getString();
			        			 channelService.sendMessage(new ChannelMessage(channelKey,json));
			        		 }
			        	 } catch (JsonProcessingException e) {
			        		 // TODO Auto-generated catch block
			        		 e.printStackTrace();
			        	 }
			        	 return new ResponseEntity<PostInfo>(newPostInfo, HttpStatus.OK);
			        }
			      }
			    } catch (Exception ex) {
			    	System.out.println(ex.getMessage());
			        throw new ServletException(ex);
			    }
			 
			 return null;
			 	
		}
		 
		//User info here only have a simple user profile name and its account Name
		 @RequestMapping(value="/{postId}", method = RequestMethod.DELETE)
		 public  ResponseEntity<PostMeta> createPost(@PathVariable String postId){

			 ofy().delete().type(PostMeta.class).id(postId).now();
			 //TODO delete all post data as well
			 return new ResponseEntity<PostMeta>(HttpStatus.NO_CONTENT);
		} 
	/**
	 *  
	 * @param first
	 * @param second
	 * @return concated array from first and second
	 */
	 public static byte[] concat(byte[] first, byte[] second) {
		 byte[] result = Arrays.copyOf(first, first.length + second.length);
		 System.arraycopy(second, 0, result, first.length, second.length);
		 return result;
	 }
	
}
