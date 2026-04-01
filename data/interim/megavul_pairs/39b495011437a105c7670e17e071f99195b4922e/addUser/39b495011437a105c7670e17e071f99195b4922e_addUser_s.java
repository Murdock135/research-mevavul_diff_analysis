class addUser {
public ResultSet addUser(){
		
	
		//String query1 = "Insert into APPUSER(USER_ID,EMAIL,PASSWORD,FIRST_NAME,LAST_NAME,IS_FACEBOOK_LOGIN)"
		//		+ " values (usr_id.NEXTVAL,'aryaa@seas.upenn.edu','test','ARyaa','Gautam','Y')";
		
		ResultSet rs = wrapper.executeQuery(query);
		return rs;
		
		
	}
}
