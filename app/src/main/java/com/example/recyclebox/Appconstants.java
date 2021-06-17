package com.example.recyclebox;

import android.content.Context;

import androidx.core.content.ContextCompat;

public class Appconstants {
    public static final String RECYCLED_COUNT_KEY = "recycledCount",POINTS_KEY = "points";
    public final static String COLLECTION_KEY_INFORMATION = "information";
    public final static String INFO_DOCUMENT_KEY = "Basic", FACT_DOCUMENT_KEY = "Facts";
    public final static String FACTLIST_KEY = "facts",ACHIEVEMENT_COLLECTION = "achievements";
    public final static String TOTAL_RECYCLED_COUNT = "totalRecycledCount";
    public final static String MAP_KEY = "recyclables";
    public final static String COLLECTION_CENTRE_KEY = "centres";
    public final static String RANK_KEY = "rank";
    public final static String BRONZE_RANK = "Bronze",SIlVER_RANK = "Silver";
    public final static String GOLD_RANK = "Gold",Plat_RANK = "Platinum";
    public final static int SILVER_RANK_POINTS = 5,GOLD_RANK_POINTS = 10;
    public final static int Plat_RANK_POINTS = 12;
    public final static int ACCOMMODATION_CODE =3;
    public final static int UNIVERSITY_CODE = 1;
    public final static int SUNDERLAND_COUNCIL_CODE = 2;
    public final static String aerosols = "Aerosols",cardboard = "Cardboard";
    public final static String metalTins = "Metal Tins and Cans",paper = "Paper";
    public final static String plasticBottles = "Plastic Bottles",plasticPackaging = "Plastic Trays and Tubs";
    public final static String glassBottles = "Glass Bottles and Jars";
    public final static String blueBin = "Blue caddy",blackInnerBin = "Black inner caddy";
    public final static String glassBin = "Glass bin", mixedRecyclingBin = "Blue mixed bin";
    public final static Integer teamType = 10,teamPlayerType = 2;

    static final int type_aerosol = 5,type_cardboard = 6, type_metal = 7, type_paper = 8, type_plasticbottles = 9;
    static final int type_plasticpackaging = 10, type_glass = 11;
    public static final int achievementTypeBronze = 1,achievementTypeSilver = 2,achievementTypeGold = 3;


    public static int returnColour(Context c,int iCode) {
        int colour = ContextCompat.getColor(c,iCode);
        return colour;
    }


    public final static int ITYPE_ALL = 20;

    public static int returnRadioButtonIndex(String sRadioButtonText) {
        int iIndex =-1;
        if (sRadioButtonText.equals(aerosols))
            iIndex = 0;
        else if (sRadioButtonText.equals(cardboard))
            iIndex = 1;
        else if (sRadioButtonText.equals(metalTins))
            iIndex = 2;
        else if (sRadioButtonText.equals(paper))
            iIndex = 3;
        else if (sRadioButtonText.equals(plasticBottles))
            iIndex = 4;
        else if (sRadioButtonText.equals(plasticPackaging))
            iIndex = 5;
        else if (sRadioButtonText.equals(glassBottles))
            iIndex = 6;
        return iIndex;
    }



    public static int returnMaterialType(String material) {
        int iIndex = 20;
        if (material.equals(aerosols))
            iIndex = type_aerosol;
        else if (material.equals(cardboard))
            iIndex = type_cardboard;
        else if (material.equals(metalTins))
            iIndex = type_metal;
        else if (material.equals(paper))
            iIndex = type_paper;
        else if (material.equals(plasticBottles))
            iIndex = type_plasticbottles;
        else if (material.equals(plasticPackaging))
            iIndex = type_plasticpackaging;
        else if (material.equals(glassBottles))
            iIndex = type_glass;
        return iIndex;
    }


}

