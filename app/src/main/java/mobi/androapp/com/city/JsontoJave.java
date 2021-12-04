package mobi.androapp.com.city;

public class JsontoJave {

   static String[] jsons = { "&#8211;","&#8212;","&#8216;","&#8217;","&#8218;","&#8220;","&#8221;","&#8222;","&#8224;","&#8225;","&#8226;","&#8230;","&#8240;","&#8364;","&#8482;"};
    static String[] java = {"–","—","‘","’","‚","“","”","„","†","‡","•","…","‰","€","™"};
    public static Object String(String input){

        for(int i=0;i<jsons.length;i++){
            input = input.replace(jsons[i],java[i]);
        }

        return input;
    }
}
