package com.lw;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        PropertiesUtil p = new PropertiesUtil("config.properties");
//        System.out.println(p.getProperties().get("config.excelPath"));
//        System.out.println(p.getProperties().get("config.columns"));

        File directory = new File(".");
        System.out.println(directory.getCanonicalPath());
        System.out.println(directory.getAbsolutePath()+"test.sql");
//        direcotry.getPath();

    }
}
