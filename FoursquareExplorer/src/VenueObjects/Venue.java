package VenueObjects;

public class Venue 
{
	String id,name,url;
	Location location;
	Photo photo;
	
	public Venue(String id,String name,String url , Location location , Photo photo) 
	{
		this.id = id;
		this.name = name;
		this.url = url;
		this.location=location;
		this.photo = photo;
	}
	
	public String getID()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public String getURL()
	{
		return url;
	}
	public Location getLocation()
	{
		return location;
	}
	public Photo getPhoto()
	{
		return photo;
	}
}
