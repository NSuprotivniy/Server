package edu.technopolis;

/**
 * Created by nsuprotivniy on 28.05.17.
 */

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle all feeds to subscribers table in database.
 */


public class FeedsHandler extends ModelHandler {

    // Class is Singleton. It makes access to subscribers in one place
    // and one connection to data base with serialized mode.
//    private static final FeedsHandler INSTANCE = new FeedsHandler();
//    public static FeedsHandler getInstance() { return INSTANCE; }


    FeedsHandler() {
        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "feeds";
        db = new DataBase(DB_PATH);
        FIND_CMD = "feeds_find";
        SAVE_CMD = "feeds_save";
        GET_CMD = "feed_get";
        ALL_CMD = "feeds_all";
    }

    FeedsHandler(DataBase db) {
        this.db = db;
        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "feeds";
        FIND_CMD = "feeds_find";
        SAVE_CMD = "feeds_save";
        GET_CMD = "feed_get";
        ALL_CMD = "feeds_all";
    }

}
