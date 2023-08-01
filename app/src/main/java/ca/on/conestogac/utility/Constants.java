package ca.on.conestogac.utility;

public class Constants {

    /*
    Server Configurations
     */
    public static final String BASE_URL = "http://10.0.2.2:8080/api/";

    /*
    Log Tracing TAG
     */
    public static final String TRACE_TAG = "CONESTOGA_TRAVEL_ASSISTANT";

    /*
    Login Status Constants
     */
    public static final String LOGIN_FAILED = "LOGIN_FAILED";
    public static final String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";

    /*
    Application Package
     */
    public static final String APP_PACKAGE = "ca.on.conestogac";

    /*
    SharedPreferences Name
     */
    public static final String SHARED_PREFERENCE = "cta_shared_preferences";

    /*
    User Profile Constants
     */
    public static final String USER_FULL_NAME = "user_full_name";
    public static final String USER_EMAIL = "user_email";

    /*
    Ride Responses from Server
     */
    public static final String RIDE_DELETED = "RIDE_DELETED";
    public static final String RIDE_NOT_FOUND = "RIDE_NOT_FOUND";

    /*
    Message Types for Communication Module
     */
    public static final String MESSAGE_TYPE_MESSAGE = "MESSAGE";
    public static final String MESSAGE_TYPE_PROFILE = "PROFILE";
    public static final String MESSAGE_TYPE_RIDE_REQUEST = "RIDE_REQUEST";

    /*
    Message Responses from Server
     */
    public static final String MESSAGE_DELETED = "MESSAGE_DELETED";
    public static final String MESSAGE_NOT_FOUND = "MESSAGE_NOT_FOUND";

    /*
    Survey Responses from Server
     */
    public static final String SURVEY_SAVED_SUCCESSFULLY = "SURVEY_SAVED_SUCCESSFULLY";
    public static final String SURVEY_NOT_SAVED = "SURVEY_NOT_SAVED";
    public static final String SURVEY_DELETED = "SURVEY_DELETED";
    public static final String SURVEY_NOT_DELETED = "SURVEY_NOT_DELETED";
    public static final String SURVEY_DATA_NOT_FOUND = "SURVEY_DATA_NOT_FOUND";
    public static final String SURVEY_ALREADY_SUBMITTED = "SURVEY_ALREADY_SUBMITTED";
    public static final String SURVEY_NOT_FOUND = "SURVEY_NOT_FOUND";
    public static final String SURVEY_VERSION = "SURVEY_VERSION";

    /*
    Ride find at server
     */
    public static final String RIDE_EXIST_WITH_DRIVER = "RIDE_EXIST_WITH_DRIVER";
    public static final String RIDE_NOT_EXIST_WITH_DRIVER = "RIDE_NOT_EXIST_WITH_DRIVER";

}
