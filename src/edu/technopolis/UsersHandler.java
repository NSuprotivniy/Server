package edu.technopolis;

/**
 * Created by nsuprotivniy on 28.05.17.
 */


/**
 * Handle all requests to users table in database.
 */


public class UsersHandler extends ModelHandler {

    // Class is Singleton. It makes access to subscribers in one place
    // and one connection to data base with serialized mode.
    private static final UsersHandler INSTANCE = new UsersHandler();
    public static UsersHandler getInstance() { return INSTANCE; }


    UsersHandler() {
        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "users";
        db = new DataBase(DB_PATH);
        FIND_CMD = "users_find";
        SAVE_CMD = "users_save";
        GET_CMD = "users_get";
        ALL_CMD = "users_all";
    }
}
