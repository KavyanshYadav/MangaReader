package com.example.mangareader;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Date;



public class MangaKakrotScraper {

    /**
     * LinformatioessnSchema
     */
    public class LessinformationSchema {
        public String ID;
        public String Title ;
        public String ImageUrl;
        public String Sysnopsis;
        public String LatestChapterName;
        public int Views;

        public LessinformationSchema(String ID,String Title,String ImageUrl,String Sysnopsis,String LatestChapterName,int Views)
        {
            this.ID = ID;
            this.ImageUrl = ImageUrl;
            this.Sysnopsis = Sysnopsis;
            this.Title = Title;
            this.Views = Views;
            this.LatestChapterName = LatestChapterName;


        }
    }

    public class Chapter {


    }

    /**
     * UrlFormatter
     */
    public class UrlFormatter {

        public String URI;


    }

    public class MoreinformationSchema extends LessinformationSchema {
        public ArrayList<ChapterInfo> chapters;
        public  String author;
        public  String status;
        public  String genres;
        public  Date updatedDate;
        public  double rating;

        // Constructor with all fields
        public MoreinformationSchema(String ID, String Title, String ImageUrl, String Sysnopsis, String LatestChapterName,
                                     int Views, ArrayList<ChapterInfo> Chapters, String alternativeTitle, String author,
                                     String status, String genres, Date updatedDate, double rating) {
            super(ID, Title, ImageUrl, Sysnopsis, LatestChapterName, Views);
            this.chapters = Chapters;
            this.author = author;
            this.status = status;
            this.genres = genres;
            this.updatedDate = updatedDate;
            this.rating = rating;
        }
    }

    public class ChapterInfo {
        public String ChapterTitle ;
        public String View;
        public String Time;
        public String href;
    }

    Random rand = new Random();
    ObjectMapper Json = new ObjectMapper();
    public String ScrappingUrl;

    public ArrayList<String> GetChapterImages(String Html) throws IOException, InterruptedException{
        Document doc = Jsoup.parse(Html);
        ArrayList<String> numbers = new ArrayList<>();
        Elements imagcontainer  = doc.select("div.body-site > div.container-chapter-reader");
        Elements img = imagcontainer.select("img");
        for (Element imge : img) {
            numbers.add(imge.attr("src"));
        }
        // for (String n : numbers ){
        //     byte[]  nd = ner.getImage(n);
        //     int randomNum  = rand.nextInt(10000) + 1;
        //     FileOutputStream fos = new FileOutputStream("C:\\Users\\HP\\Desktop\\myjava\\n" + //
        //                         "a\\app\\src\\main\\java\\n" + //
        //                         "a\\lib\\n" + //
        //                         ""+ randomNum+".jpg");
        //     fos.write(nd);
        //     fos.close();
        //     System.out.println(n);
        //     System.out.println(nd);
        // }
        return numbers;
    }

    public ArrayList<LessinformationSchema> GetRecentChapters(String html) throws IOException, InterruptedException {
        Document doc = Jsoup.parse(html);
        ArrayList<LessinformationSchema> mainreturn = new ArrayList<>();

        Elements contentContainer = doc.getElementsByClass("panel-content-homepage").select("div > div");
        for (Element n : contentContainer) {
            try {
                // Check if "a" element exists
                Element linkElement = n.selectFirst("a");
                if (linkElement == null) continue;

                String href = linkElement.attr("href");
                if (href == null || !href.contains("-")) continue; // Check if href is valid

                String[] parts = href.split("-");
                String ID = parts[parts.length - 1];

                // Check if "img" element exists and has a "src" attribute
                String imageUrl = n.select("a > img").attr("src");
                if (imageUrl == null || imageUrl.isEmpty()) continue;

                // Check if title element exists
                Element titleElement = n.selectFirst("div.content-homepage-item-right > h3 > a");
                if (titleElement == null) continue;
                String title = titleElement.text();

                // Check if author element exists
                Element authorElement = n.selectFirst("div.content-homepage-item-right > span");
                String author = authorElement != null ? authorElement.text() : "Unknown"; // Set to "Unknown" if null

                mainreturn.add(new LessinformationSchema(ID, title, imageUrl, title, author, 0));
            } catch (Exception e) {
                System.err.println("Error processing element: " + e.getMessage());
                // Continue to the next element if an exception occurs
            }
        }
        return mainreturn;
    }


    public MoreinformationSchema GetMangaInfo (String Html) throws IOException , InterruptedException {
        Document doc = Jsoup.parse(Html);
        MoreinformationSchema mainreturn = new MoreinformationSchema("", "", "Html", "Html", "Html", 0, null, "Html", "Html", "Html", "Html", null, 0);
        Element Mainbody = doc.getElementsByClass("panel-story-info").first();
        Element ChapterContainer = doc.getElementsByClass("panel-story-chapter-list").first();
        ArrayList<ChapterInfo> ChapterArrayJson = new ArrayList<ChapterInfo>();
        Elements Chapters = ChapterContainer.select("ul > li");

        for(Element n : Chapters){
            ChapterInfo jk = new ChapterInfo();
            jk.ChapterTitle = n.select("a").text();
            jk.Time = n.select("span").get(1).text();
            jk.View = n.select("span").first().text();
            jk.href = n.select("a").attr("href");
            ChapterArrayJson.add(jk);
        }
        mainreturn.chapters = ChapterArrayJson;

        Element n = Mainbody.select("div.story-info-right").first();
        mainreturn.Title =  n.select("h1").first().text();
        // System.out.println(ChapterArrayJson);
        String retu = Json.writeValueAsString(mainreturn);
        Log.d("Scraper" , "return:" + retu);
        return mainreturn;

    }
}
