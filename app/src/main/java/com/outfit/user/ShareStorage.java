package com.outfit.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class ShareStorage {

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getInstance(Context context) {
        sharedPreferences = context.getSharedPreferences("outfit", Context.MODE_PRIVATE);
       return sharedPreferences;
    }

    public static void setCartData(Context context, CartModel cartModel) {
        getInstance(context).edit().putString("cartData", new Gson().toJson(cartModel)).commit();
    }

    public static CartModel getCartData(Context context) {
        return new Gson().fromJson(getInstance(context).getString("cartData", ""), CartModel.class);
    }

    public static void clear(Context context) {
        getInstance(context).edit().clear().commit();
    }
}
