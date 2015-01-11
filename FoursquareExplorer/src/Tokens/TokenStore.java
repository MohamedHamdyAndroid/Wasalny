package Tokens;

public class TokenStore 
{
	private static TokenStore sInstance;
	private String token;
	
	public static TokenStore get() 
	{
		if (sInstance == null) 
		{
			sInstance = new TokenStore();
		}
		
		return sInstance;
	}
	
	public String getToken() 
	{
		return token;
	}
	
	public void setToken(String token) 
	{
		this.token = token;
	}
}