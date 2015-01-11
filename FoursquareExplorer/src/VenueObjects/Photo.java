package VenueObjects;

public class Photo 
{
	String prefix,suffix;
	
	public Photo(String prefix,String suffix) 
	{
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	public String getSuffix()
	{
		return suffix;
	}
	
	public String getPhotoURL()
	{
		String url = prefix+"100x100"+suffix;
		return url;
	}
}
