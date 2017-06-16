import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;

/**
 * Created by Антон on 14.06.2017.
 */
public class Serializer {
    public static void main (String[] args){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("Waprg7hm4WMuUlMk3KSxFoDSQ")
                .setOAuthConsumerSecret("ZHjyK0DTW1HL2a06QEJZuHp89oHN7uuESGdtYoxEdDMDB1jeyi");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            System.out.println("-----");

            // get request token.
            // this will throw IllegalStateException if access token is already available
            // this is oob, desktop client version
            RequestToken requestToken = twitter.getOAuthRequestToken();

            System.out.println("Got request token.");
            System.out.println("Request token: " + requestToken.getToken());
            System.out.println("Request token secret: " + requestToken.getTokenSecret());

            System.out.println("|-----");

            AccessToken accessToken = null;

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (null == accessToken) {
                System.out.println("Open the following URL and grant access to your account:");
                System.out.println(requestToken.getAuthorizationURL());
                FileWriter fr = new FileWriter("URL.txt");
                fr.write(requestToken.getAuthorizationURL());
                fr.flush();
                fr.close();
                System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
                String pin = br.readLine();

                try {
                    if (pin.length() > 0) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken(requestToken);
                    }
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
            }
            System.out.println("Got access token.");
            System.out.println("Access token: " + accessToken.getToken());
            System.out.println("Access token secret: " + accessToken.getTokenSecret());
            String name = twitter.getScreenName();
            FileOutputStream fo = new FileOutputStream(name+".obj");
            ObjectOutputStream oo = new ObjectOutputStream(fo);
            oo.writeObject(accessToken);
            br.readLine();

        } catch (IllegalStateException ie) {
            // access token is already available, or consumer key/secret is not set.
            if (!twitter.getAuthorization().isEnabled()) {
                System.out.println("OAuth consumer key/secret is not set.");
                System.exit(-1);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
