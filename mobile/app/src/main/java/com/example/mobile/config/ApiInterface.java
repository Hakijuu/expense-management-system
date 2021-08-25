package com.example.mobile.config;

import com.example.mobile.model.Account;
import com.example.mobile.model.Category;
import com.example.mobile.model.Invitation;
import com.example.mobile.model.LoginForm;
import com.example.mobile.model.Unit;
import com.example.mobile.model.UpdatePasswordHolder;
import com.example.mobile.model.User;
import com.example.mobile.model.WalletHolder;
import com.example.mobile.model.WalletDetail;
import com.example.mobile.model.WalletItem;
import com.google.gson.JsonObject;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface  {
    @POST("login")
    Call<JsonObject> login(@Body LoginForm loginForm);

    @POST("register")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> register(@Body User user);

    @PUT("account/change-password")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> changePassword(@Header("Authorization") String accessToken, @Body UpdatePasswordHolder updatePasswordHolder);

    @GET("account")
    @Headers("Content-Type: application/json")
    Call<Account> getAccount(@Header("Authorization") String accessToken);

    @GET("notifications/invitations")
    @Headers("Content-Type: application/json")
    Call<List<Invitation>> getInvitations(@Header("Authorization") String accessToken);

    @PUT("notifications/invitations/{id}")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> manageInvitation(@Header("Authorization") String accessToken, @Path("id") int id, @Body Boolean flag);

    @GET("wallets")
    @Headers("Content-Type: application/json")
    Call<List<WalletItem>> getUserWallets(@Header("Authorization") String accessToken);

    @POST("create-wallet")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> createWallet(@Header("Authorization") String accessToken, @Body WalletHolder walletHolder);

    @GET("wallet/{id}")
    @Headers("Content-Type: application/json")
    Call<WalletDetail> getWalletById(@Header("Authorization") String accessToken, @Path("id") int id);

    @GET("categories")
    @Headers("Content-Type: application/json")
    Call<List<Category>> getCategories();

    @GET("units")
    @Headers("Content-Type: application/json")
    Call<List<Unit>> getUnits();

    @GET("{infix}")
    @Headers("Content-Type: application/json")
    Call<List<String>> getMembersByInfix(@Header("Authorization") String accessToken, @Path("infix") String infix);
}