package Handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import VenueObjects.Location;
import VenueObjects.Photo;
import VenueObjects.Venue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper 
{
	
    private static final int databaseVersion = 1;
 
    // Database Name
    private static final String databaseName = "SnapshotsDB";
 
    // Contacts table name
    private static final String tableVenues = "Venues";
 
    // Contacts Table Columns names
    public static final String key_ID = "ID";
    public static final String key_VenueID = "VenueID";
    public static final String key_Name = "Name";
    public static final String key_Url = "URL";
    public static final String key_LocationAddress = "address";
    public static final String key_LocationCrossStreet = "CrossStreet";
    public static final String key_LocationLat = "Lat";
    public static final String key_LocationLng = "Lng";
    public static final String key_LocationDistance = "Distance";
    public static final String key_PhotoPrefix = "Prefix";
    public static final String key_PhotoSuffix = "Uploaded";
    
    public DatabaseHandler(Context context) 
    {
        super(context, databaseName, null, databaseVersion);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
    	//Venues table creation
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + tableVenues + "("
                + key_ID 					+ " INTEGER PRIMARY KEY autoincrement,"
                + key_VenueID 				+ " TEXT not null,"
        		+ key_Name 					+ " TEXT,"
        		+ key_Url 					+ " TEXT,"
        		+ key_LocationAddress 		+ " TEXT,"
        		+ key_LocationCrossStreet 	+ " TEXT,"
        		+ key_LocationLat 			+ " TEXT,"
        		+ key_LocationLng 			+ " TEXT,"
        		+ key_LocationDistance 		+ " TEXT,"
        		+ key_PhotoPrefix 			+ " TEXT,"
        		+ key_PhotoSuffix			+ " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + tableVenues);
        // Create tables again
        onCreate(db);
    }
    
    public void deleteAll() 
    {
        // Drop older table if existed
    	SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableVenues);
        // Create tables again
        onCreate(db);
        db.close();
    }
    
    public void addVenue(Venue snapshot) 
    {
        SQLiteDatabase mydb = this.getWritableDatabase();    
        ContentValues values = new ContentValues();
        values.put(key_VenueID, snapshot.getID());
        values.put(key_Name, snapshot.getName());
        values.put(key_Url, snapshot.getURL());
        values.put(key_LocationAddress, snapshot.getLocation().getAddress());
        values.put(key_LocationCrossStreet, snapshot.getLocation().getCrossStreet());
        values.put(key_LocationLat, snapshot.getLocation().getLat());
        values.put(key_LocationLng, snapshot.getLocation().getLng());
        values.put(key_LocationDistance, snapshot.getLocation().getDistance());
        values.put(key_PhotoPrefix, snapshot.getPhoto().getPrefix());
        values.put(key_PhotoSuffix, snapshot.getPhoto().getSuffix());
        // Inserting Row
        mydb.insert(tableVenues, null, values);
        mydb.close(); 
    }
    
    public void addVenueList(ArrayList<Venue> allVenues) 
    {
    	try
    	{
    		SQLiteDatabase mydb = this.getWritableDatabase();
    		for(int counter=0;counter<allVenues.size();counter++)
    		{
    			Venue snapshot = allVenues.get(counter);
    			ContentValues values = new ContentValues();
    			
    			values.put(key_VenueID, snapshot.getID());
    			values.put(key_Name, snapshot.getName());
    			values.put(key_Url, snapshot.getURL());
    			values.put(key_LocationAddress, snapshot.getLocation().getAddress());
    			values.put(key_LocationCrossStreet, snapshot.getLocation().getCrossStreet());
    			values.put(key_LocationLat, snapshot.getLocation().getLat());
    			values.put(key_LocationLng, snapshot.getLocation().getLng());
    			values.put(key_LocationDistance, snapshot.getLocation().getDistance());
    			values.put(key_PhotoPrefix, snapshot.getPhoto().getPrefix());
    			values.put(key_PhotoSuffix, snapshot.getPhoto().getSuffix());
    			
    			mydb.insert(tableVenues, null, values);
    		}
    		
    		mydb.close();
    	}
    	catch (Exception e) 
    	{
			Log.e("Exception", e.getMessage());
		}
    	catch (Error e) 
    	{
			Log.e("Exception", e.getMessage());
		}
    }
    
    public ArrayList<Venue> getAllVenues() 
    {
    	ArrayList<Venue> allVenueList = new ArrayList<Venue>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableVenues;
     
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) 
        {
            do 
            {
            	Location location = new Location(cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
            	Photo venuePhoto = new Photo(cursor.getString(9), cursor.getString(10));
            	Venue oneVenue = new Venue(cursor.getString(1), cursor.getString(2), cursor.getString(3), location, venuePhoto);
            	
                allVenueList.add(oneVenue);
                
            } 
            while (cursor.moveToNext());
        }
     
        // return contact list
        return allVenueList;
    }

        
}
