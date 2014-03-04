package database;

/**
 * There can only be one of this class.
 * Used to connect from classes to the database handler.
 * @author Aleksandar Jonovic
 */
public class Database 
{
    // The database interface
    private static DatabaseInterface db = new DatabaseHandler();;
    
    /**
     * This method gets the database interface for use.
     * @return DatabaseHandler as DatabaseInterface.
     */
    public static DatabaseInterface db()
    {
        return db;
    }
}