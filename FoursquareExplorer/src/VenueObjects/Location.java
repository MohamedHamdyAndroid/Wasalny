package VenueObjects;

public class Location 
{
	String address,crossStreet,lat,lng,distance;
	
	public Location(String address,String crossStreet,String lat,String lng,String distance) 
	{
		this.address = address;
		this.crossStreet = crossStreet;
		this.lat = lat;
		this.lng = lng;
		this.distance = distance;
	}
	
	public String getAddress()
	{
		return address;
	}
	public String getCrossStreet()
	{
		return crossStreet;
	}
	public String getLat()
	{
		return lat;
	}
	public String getLng()
	{
		return lng;
	}
	public String getDistance()
	{
		return distance;
	}
}
