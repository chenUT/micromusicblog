package com.ece1779.group4.mmb.dao;

import com.ece1779.group4.mmb.model.PostData;
import com.ece1779.group4.mmb.model.PostMeta;
import com.ece1779.group4.mmb.model.UserInfo;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
	 static {
	        factory().register(PostMeta.class);
	        factory().register(UserInfo.class);
	        factory().register(PostData.class);
	    }

	    public static Objectify ofy() {
	        return ObjectifyService.ofy();
	    }

	    public static ObjectifyFactory factory() {
	        return ObjectifyService.factory();
	    }
}
