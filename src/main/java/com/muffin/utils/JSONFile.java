package com.muffin.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONFile {

    private static final String fileName = "discordConf.json";

    private static void createFile() {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
                System.out.println("[INFO]: File created on " + f.getPath());
            } catch (IOException e) {
                System.err.println("[ERROR]: File cannot be created");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addInFile(String token, String channelId) {
        createFile();
        JSONObject data = new JSONObject();
        data.put("token", token);
        data.put("channelId", channelId);
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(data.toJSONString());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getDataFile() {
        String token = "";
        String channelId = "";
        JSONParser parser = new JSONParser();
        try (FileReader fr = new FileReader(fileName)) {
            Object o = parser.parse(fr);
            JSONObject data = (JSONObject) o;
            token = (String) data.get("token");
            channelId = (String) data.get("channelId");
        } catch (IOException e) {
            System.err.println("[ERROR]: Error on reading");
            createFile();
        } catch (ParseException e) {
            System.err.println("[ERROR]: Error on parsing");
        }

        return new String[] {token, channelId};
    }

}
