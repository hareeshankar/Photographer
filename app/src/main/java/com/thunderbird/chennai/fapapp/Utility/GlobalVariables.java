package com.thunderbird.chennai.fapapp.Utility;

/**
 * Created by piyush87530 on 02-06-2017.
 */

public class GlobalVariables {

    public  static String  selectedEquipmentId;

    public  static String  selectedSpecialityId;

    public  static  boolean[] reader_selections;

    public  static  boolean[] reader_selections_equipment;
    public  static  String str1;
    public  static  String str2;


    public static boolean[] getReader_selections() {
        return reader_selections;
    }

    public static void setReader_selections(boolean[] reader_selections) {
        GlobalVariables.reader_selections = reader_selections;
    }

    public static boolean[] getReader_selections_equipment() {
        return reader_selections_equipment;
    }

    public static void setReader_selections_equipment(boolean[] reader_selections_equipment) {
        GlobalVariables.reader_selections_equipment = reader_selections_equipment;
    }

    public static String getStr1() {
        return str1;
    }

    public static void setStr1(String str1) {
        GlobalVariables.str1 = str1;
    }

    public static String getStr2() {
        return str2;
    }

    public static void setStr2(String str2) {
        GlobalVariables.str2 = str2;
    }

    public static String getSelectedEquipmentId() {
        return selectedEquipmentId;
    }

    public static void setSelectedEquipmentId(String selectedEquipmentId) {
        GlobalVariables.selectedEquipmentId = selectedEquipmentId;
    }

    public static String getSelectedSpecialityId() {
        return selectedSpecialityId;
    }

    public static void setSelectedSpecialityId(String selectedSpecialityId) {
        GlobalVariables.selectedSpecialityId = selectedSpecialityId;
    }
}
