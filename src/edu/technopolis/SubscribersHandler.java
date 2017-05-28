package edu.technopolis;

import org.sqlite.core.DB;

/**
 * Created by nsuprotivniy on 28.05.17.
 */

/**
 * Handle all requests to subscribers table in database.
 */


public class SubscribersHandler extends ModelHandler {

    // Class is Singleton. It makes access to subscribers in one place
    // and one connection to data base with serialized mode.
    private static final SubscribersHandler INSTANCE = new SubscribersHandler();
    public static SubscribersHandler getInstance() { return INSTANCE; }

    // Database with posts table.
    private DataBase db;


    SubscribersHandler() {
        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "subscribers";
        db = new DataBase(DB_PATH);
        FIND_CMD = "subscribers_find";
        SAVE_CMD = "subscribers_save";
        GET_CMD = "subscribers_get";
        ALL_CMD = "subscribers_all";
    }

}
