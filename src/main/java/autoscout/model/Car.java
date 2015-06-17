package autoscout.model;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Julien on 17/06/15.
 */
public class Car {

    private int id;
    private URL url;

    private Date update = new Date();


    private List<String> equipements = new ArrayList<String>();


    public Car(URL url) throws IOException {
        this.url = url;
        this.equipements = new ArrayList<String>();

        Document doc = Jsoup.connect(url.toString() + "&tabid=1").get();
        Element elt = doc.body().getElementById("equipmentBoxGrouped");
        for(Element lstElt : elt.getElementsByClass("textlist-item")) {
            String equipement = StringEscapeUtils.unescapeHtml(lstElt.html());
            // System.out.println(equipement);
            if(!equipements.contains(equipement))
                equipements.add(equipement);
        }

    }


    public static void main(String[] args) throws IOException {
        URL url = new URL("http://www.autoscout24.ch/fr/d/bmw-320-break-2012-occasion?allmakes=1&equip=36&equipor=33554432&index=4&make=9%2c0%2c0&model=46%2c0%2c0&priceto=35000&returnurl=%2ffr%2fvoitures%2fbmw--3-series%3fallmakes%3d1%26equip%3d36%26equipor%3d33554432%26make%3d9%2c0%2c0%26model%3d46%2c0%2c0%26priceto%3d35000%26sort%3dyear_asc%26st%3d2%26trans%3d21%26vehtyp%3d10%26yearfrom%3d2012%26r%3d5&sort=year_asc&st=2&trans=21&vehid=2968258&vehtyp=10&yearfrom=2012&lng=de");
        new Car(url);
    }

}
