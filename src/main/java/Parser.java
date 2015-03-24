import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;


public class Parser {
    public URL theatreUrl;
    public URL theatreRep;
    public static void getTheatres() throws IOException {
        try {
            Document theatre = Jsoup.connect("https://afisha.yandex.ru/msk/places/?category=theatre").get();
            // &limit=1000
            Elements places = theatre.getElementsByClass("place");
            // счетчик для индексов
            int i = 0;
            for (Element place : places) {
                //Map<String, Map> theatreFinally = new HashMap<String, Map>();
                Map<String, Object> theatreMap = new HashMap<String, Object>();
                //List nameList = new ArrayList();
                //List addressList = new ArrayList();
                List metroList = new ArrayList();
                // название
                String placeName = place.getElementsByClass("place_title").text();
                //nameList.add(placeName);
                // адрес
                String placeAddress = place.getElementsByTag("a").last().text();
                //addressList.add(placeAddress);
                // получение координат
                theatreMap.putAll(Parser.getGeocoding(
                        "http://maps.googleapis.com/maps/api/geocode/json?address=",
                        placeAddress+" Москва",
                        "&sensor=false&language=russian"));
                // метро
                String placeMetro = place.getElementsByTag("td").last().text();
                metroList.add(placeMetro);
                // URL
                String placeUrl = place.getElementsByClass("place_title").attr("href");
                System.out.println(placeUrl);
                theatreMap.put("name", placeName);
                //theatreMap.put("address", addressList);
                theatreMap.put("metro", metroList);
                // доп сведения
                theatreMap.putAll(Parser.getTheatreRep(placeName, placeUrl));
                //theatreFinally.put(placeName, theatreMap);
                // Map
                // name => address, metro, name, data, rep
                //JSONObject jsonTheatre = new JSONObject();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonTheatre = gson.toJson(theatreMap).toString();
                System.out.println(jsonTheatre);
                Parser.saveJson(placeName, jsonTheatre);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Map getTheatreRep(String name, String url){
        Map<String, List> theatreInfoMap = new HashMap<String, List>();
        // контактная информация
        List places = new ArrayList();
        // репертуар
        List rep = new ArrayList();
        try {
            Document theatreInfo = Jsoup.connect("http://www.afisha.yandex.ru"+url).get();
            // информация
            Elements placeInfo = theatreInfo.getElementsByClass("b-place__charact-list");
            for (Element place : placeInfo){
                // данные
                String placeData = place.getElementsByTag("p").text();
                places.add(placeData);
            }
            // репертуар
            Elements placeRep = theatreInfo.getElementsByClass("b-schedule-table__event");
            for (Element place : placeRep){
                rep.add(place.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        theatreInfoMap.put("data", places);
        theatreInfoMap.put("rep", rep);
        return theatreInfoMap;
    }
    private static Map getGeocoding(String url, String address, String sensor) {
        // Адрес и координаты
        Map<String, String> result = new HashMap<String, String>();
        try {
            URL geoUrl = new URL(url+URLEncoder.encode(address, "UTF-8")+sensor);
            System.out.println(geoUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(geoUrl.openStream()));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            JSONObject geo = new JSONObject(sb.toString());
            JSONArray results = geo.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject row = results.getJSONObject(i);
                // полный адрес
                String fullAddress = row.getString("formatted_address");
                JSONObject geometry = row.getJSONObject("geometry");
                // координаты
                JSONObject jsonCoord = geometry.getJSONObject("location");
                result.put("address", fullAddress);
                // TODO разобраться с координатами lat и lng
                result.put("coord", jsonCoord.toString());
            }
            System.out.println(result);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static Boolean saveJson(String theatreName, String theatre) throws IOException {
        String workingDir = System.getProperty("user.dir");
        FileWriter file = new FileWriter(workingDir+"\\json\\"+theatreName+".json");
        try {
            file.write(theatre);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            file.flush();
            file.close();
        }
    }
}