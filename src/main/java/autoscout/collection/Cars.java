package autoscout.collection;

import autoscout.model.Car;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Julien on 17/06/15.
 */
public class Cars {

    private static String filename = "cars.json";

    private static HashMap<Integer, autoscout.model.Car> instance = null;

    public static HashMap<Integer, autoscout.model.Car> getInstance() throws IOException {
        if (instance == null) {
            try {
                instance = loadCars(filename);
                System.out.println("Load from file");
            } catch (Exception e) {
                instance = new HashMap<Integer, autoscout.model.Car>();
                saveCars(filename);
                instance = loadCars(filename);
                System.out.println("Create new file");
            }
        }

        return instance;
    }

    private Cars() {

    }

    public static void updateCollection(URL url) throws IOException {
        if (instance == null)
            getInstance();



            do {
                Document doc = Jsoup.connect(url.toString()).get();

                for(Element e : doc.getElementsByClass("object-data")) {
                    URL urlbis = new URL(e.select("a").first().attr("abs:href"));


                    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
                    String query = urlbis.getQuery();
                    String[] pairs = query.split("&");
                    for (String pair : pairs) {
                        int idx = pair.indexOf("=");
                        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }

                    int id = Integer.parseInt(query_pairs.get("vehid"));
                    if(!instance.containsKey(id)) {
                        try {
                            instance.put(id, new Car(urlbis));
                        }catch(Exception e2) {}
                    }
                }




                String next = doc.getElementById("listpaging").getElementsByClass("next").last().attr("abs:href");

                if(next.equals(""))
                    break;
                System.out.println(": " + next);

                url = new URL(next);
            } while (true);

        saveCars(filename);
    }

    public static void main(String[] args) throws IOException {
        URL url = new URL("http://www.autoscout24.ch/fr/voitures/bmw--3-series?allmakes=1&equip=36&equipor=33554432&make=9%2c0%2c0&model=46%2c0%2c0&priceto=35000&sort=year_asc&st=2&trans=21&vehtyp=10&yearfrom=2012#lnkDetail3231783");

        Cars.updateCollection(url);
    }

    private static HashMap<Integer, autoscout.model.Car> loadCars(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<Integer, autoscout.model.Car> cars = gson.fromJson(br, new TypeToken<HashMap<Integer, autoscout.model.Car>>() {
        }.getType());
        br.close();

        return cars;
    }

    private static void saveCars(String filename) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(filename);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        pw.write(gson.toJson(instance));
        pw.close();
    }


}
