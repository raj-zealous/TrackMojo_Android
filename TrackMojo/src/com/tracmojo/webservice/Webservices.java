package com.tracmojo.webservice;

public class Webservices {
	//Server links..

 
	//	private static String SERVER_BASE_URL="http://app.tracmojo.com/backend/web/api/user/";
	//private static String SERVER_BASE_URL="http://54.153.145.178/backend/web/api/user/";
    
//		/*local url */
//	private static String SERVER_BASE_URL="http://opensource.zealousys.com/backend/web/api/user/";
//     
	
	/*live url */
	private static String SERVER_BASE_URL="http://54.153.145.178/backend_new/web/api/user/";
     
	 
	 
	//api links
	public static String LOGIN = SERVER_BASE_URL +  "login";
	public static String FORGOT_PASSWORD = SERVER_BASE_URL +  "forgotpassword";
	public static String REGISTER = SERVER_BASE_URL +  "register";
    public static String CHECK_SOCIAL_USER = SERVER_BASE_URL +  "checksocialid";
    public static String SOCIAL_LOGIN = SERVER_BASE_URL +  "loginviasocial";
    public static String PRIVACY_POLICY = SERVER_BASE_URL +  "getcmsdetails";
    public static String GET_TRAC_LIST = SERVER_BASE_URL +  "gettraclist";
    public static String ADD_RATE = SERVER_BASE_URL +  "addrate";
    public static String GET_DATA_WHILE = SERVER_BASE_URL +  "getdatawhileaddtrac";
    public static String ADD_PERSONAL_TRAC = SERVER_BASE_URL +  "addpersonaltrac";
    public static String EDIT_PERSONAL_TRAC = SERVER_BASE_URL +  "editpersonaltrac";
    public static String GET_TRAC_DETAIL = SERVER_BASE_URL +  "gettracdetails";
    public static String SEND_NOTIFICATION = SERVER_BASE_URL +  "sendpushtocontact";
    public static String SEND_EMAIL = SERVER_BASE_URL +  "sendemailtocontact";
    public static String SYNC_OFFLINE_DATA = SERVER_BASE_URL +  "syncofflinedata";
    public static String DELETE_TRAC = SERVER_BASE_URL +  "deletetrac";
    public static String RESPOND_TO_INVITATION = SERVER_BASE_URL +  "respondtracinvitation";
    public static String ADD_GROUP_TRAC = SERVER_BASE_URL +  "addgrouptrac";
    public static String EDIT_GROUP_TRAC = SERVER_BASE_URL +  "editgrouptrac";
    public static String ADD_COMMENT = SERVER_BASE_URL +  "addcomment";
    public static String GET_COMMENT_LIST = SERVER_BASE_URL +  "getcommentlist";
    public static String GET_CMS = SERVER_BASE_URL +  "getcmsdetails";
    public static String GET_NOTIFICATION_SETTINGS = SERVER_BASE_URL +  "getsetting";
    public static String SET_NOTIFICATION_SETTINGS = SERVER_BASE_URL +  "setsetting";
    public static String GET_USER_DETAIL = SERVER_BASE_URL +  "getprofile";
    public static String UPDATE_USER_DETAIL = SERVER_BASE_URL +  "updateprofile";
    public static String GET_USER_PLANS = SERVER_BASE_URL +  "getallplan";
    public static String LOGOUT = SERVER_BASE_URL +  "logout";
    public static String EMAIL_COMMENT_LIST = SERVER_BASE_URL +  "sendcomments";
    public static String ADD_FOLLOWERS = SERVER_BASE_URL +  "addfollowers";
    public static String ADD_PARTICIPANTS = SERVER_BASE_URL +  "addparticipants";
    public static String GET_CMS_DETAILS = SERVER_BASE_URL +  "getcmsdetails";
    public static String INVITE_API = SERVER_BASE_URL +  "confirmsponsoredtrac";
    public static String INVITE_OK_API = SERVER_BASE_URL +  "addsponsoredtrac";
    

}
