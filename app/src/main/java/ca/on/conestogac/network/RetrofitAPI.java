package ca.on.conestogac.network;

import java.util.List;

import ca.on.conestogac.model.Feedback;
import ca.on.conestogac.model.LoginStatus;
import ca.on.conestogac.model.Message;
import ca.on.conestogac.model.RidePost;
import ca.on.conestogac.model.Survey;
import ca.on.conestogac.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

    /*
    User Object API Request
     */
    @POST("users")
    Call<User> createUser(@Body User user);

    @POST("users/login")
    Call<LoginStatus> login(@Body User user);

    @GET("users/userprofile/{email}")
    Call<User> getUserProfile(@Path("email") String email);

    /*
    RidePost Object API Request
     */
    @POST("rides")
    Call<RidePost> postRide(@Body RidePost ridePost);

    @GET("rides/email")
    Call<List<RidePost>> getRidesList(@Query("email") String email);

    @GET("rides/latest")
    Call<List<RidePost>> getLatestRidesList();

    @DELETE("rides/rideId")
    Call<String> deleteRideById(@Query("rideId") Integer rideId);


    /*
    Message Object API Request
     */
    @GET("messages/{rideId}")
    Call<List<Message>> getMessages(@Path("rideId") int rideId);

    @POST("messages")
    Call<Message> postMessage(@Body Message message);

    @DELETE("messages/messageId")
    Call<String> deleteMessageById(@Query("messageId") Integer messageId);

    @PUT("messages/messageId/{messageId}/status/{statusId}")
    Call<Message> updateRideStatus(@Path("messageId") Integer messageId, @Path("statusId") Integer statusId);

    @GET("messages/driver/{driver}/user/{user}")
    Call<String> findRideWithDriver(@Path("driver") String driverEmail, @Path("user") String userEmail);

    /*
    Survey Object API Request
     */
    @POST("survey")
    Call <String> postSurveyResponse(@Body List<Survey> surveyList);

    @DELETE("survey/email")
    Call<String> deleteSurveyResponses(@Query("email") String email);

    @GET("survey/email")
    Call<Survey> findSurvey(@Query("email") String email);

    @GET("feedback/driver/{driverEmail}")
    Call<List<Feedback>> getDriverFeedbackList(@Path("driverEmail") String driverEmail);

    @GET("feedback/driver/{driver}/user/{user}")
    Call<Feedback> getFeedback(@Path("driver") String driverEmail, @Path("user") String userEmail);

    @POST("feedback")
    Call<Feedback> postFeedback(@Body Feedback feedback);

    @PUT("feedback")
    Call<Feedback> updateFeedback(@Body Feedback feedback);

}
