package com.titan.foodrecipes.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;

@Entity(tableName = "recipes")
public class Recipe implements Parcelable {

    @PrimaryKey
    @NonNull
    private String recipe_id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "publisher")
    private String publisher;

    @ColumnInfo(name = "ingredients")
    private String ingredients[];

    @ColumnInfo(name = "image_url")
    private String image_url;

    @ColumnInfo(name = "social_rank")
    private float social_rank;

    @ColumnInfo(name = "timeStamp")
    private int timeStamp;

    @Ignore
    public Recipe() {

    }

    public Recipe(@NonNull String recipe_id, String title, String publisher, String[] ingredients, String image_url, float social_rank, int timeStamp) {
        this.recipe_id = recipe_id;
        this.title = title;
        this.publisher = publisher;
        this.ingredients = ingredients;
        this.image_url = image_url;
        this.social_rank = social_rank;
        this.timeStamp = timeStamp;
    }

    @Ignore
    protected Recipe(Parcel in) {
        title = in.readString();
        publisher = in.readString();
        ingredients = in.createStringArray();
        recipe_id = in.readString();
        image_url = in.readString();
        social_rank = in.readFloat();
        timeStamp = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(publisher);
        dest.writeStringArray(ingredients);
        dest.writeString(recipe_id);
        dest.writeString(image_url);
        dest.writeFloat(social_rank);
        dest.writeInt(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public float getSocial_rank() {
        return social_rank;
    }

    public void setSocial_rank(float social_rank) {
        this.social_rank = social_rank;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipe_id='" + recipe_id + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", ingredients=" + Arrays.toString(ingredients) +
                ", image_url='" + image_url + '\'' +
                ", social_rank=" + social_rank +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
