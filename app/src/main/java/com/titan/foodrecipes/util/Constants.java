package com.titan.foodrecipes.util;

public class Constants {

    //food2fork does not work anymore
    //public static final String BASE_URL = "https://www.food2fork.com";
    // public static final String API_KEY = "4a4476650d4e1b3923fbd1ae5cbd3ec6";//"d3ab033003c2e546e131f5b45402e3e9";//"bfe5b89b70c37ff3a0d7731b8eac07e3";

    //Alternativa para o food2fork
    public static final String BASE_URL = "https://recipesapi.herokuapp.com";
    public static final String API_KEY = "";

    public static final int CONNECTION_TIMEOUT = 10; // 10 seconds
    public static final int READ_TIMEOUT = 20; // 2 seconds
    public static final int WRITE_TIMEOUT = 20; // 2 seconds

    public static final int NETWORK_TIMEOUT = 3000;


    public static final int RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; // 30 days in seconds

    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };

}
