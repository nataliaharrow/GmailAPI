package project.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.api.services.gmail.model.MessagePart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import project.domain.Invitation;
import project.persistence.InvitationRepository;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InboxService {

    private static final String APPLICATION_NAME = "GmailTest";

    //  HTTP transport layer allows you to build on top of the low-level HTTP of your choice and optimize for the
    //  Java platform your application is running on.
    private static HttpTransport httpTransport;

    //  The main factory class of Jackson package, used to configure and construct reader (aka parser, JsonParser) and
    //  writer (aka generator, JsonGenerator) instances
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    //Service definition for Gmail
    //Access Gmail mailboxes including sending user email.
    private static Gmail client;

    @Autowired
    private InvitationRepository invitationRepository;

    //OAuth 2.0 client secrets JSON model
    GoogleClientSecrets clientSecrets;

    //OAuth 2.0 authorization code flow that manages and persists end-user credentials
    //Designed to simplify the flow in which an end-user authorizes the application to access
    //Their protected data, and then the application has access to their data based on an access
    //Token and a refresh token to refresh that access token when it expires.
    GoogleAuthorizationCodeFlow flow;

    Credential credential;

    GoogleCredential googleCredential;

    protected InboxService() {
    }

    public InboxService(GoogleClientSecrets clientSecrets, GoogleAuthorizationCodeFlow flow,
                        Credential credential, String clientId, String clientSecret, String redirectUri,
                        InvitationRepository invitationRepository,  GoogleCredential googleCredential) {
        this.clientSecrets = clientSecrets;
        this.flow = flow;
        this.credential = credential;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.invitationRepository = invitationRepository;
        this.googleCredential = googleCredential;
    }

    //    @Value used for injecting values into fields in Spring-managed beans
    @Value("${gmail.client.clientId}")
    private String clientId;

    @Value("${gmail.client.clientSecret}")
    private String clientSecret;

    @Value("${gmail.client.redirectUri}")
    private String redirectUri;

    public boolean gmailConnection() throws Exception {

        AuthorizationCodeRequestUrl authorizationUrl;

        //  Create flow
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(GmailScopes.MAIL_GOOGLE_COM)).build();
        }

        //  redirect url to the authorization page
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).setAccessType("offline");

        System.out.println("gmail authorizationUrl ->" + authorizationUrl);
        String urlAccess = authorizationUrl.build();

        // Open url from the application
//        Desktop desktop = Desktop.getDesktop();
//        desktop.browse(new URI(urlAccess));

        return true;
    }

    public HashMap<String, String> findBouncedEmails(String code) {

        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        HashMap<String, String> invalidEmailAddresses = new HashMap<>();

        // String message;
        try {
            //  TokenResponse --> OAuth 2.0 JSON model for a successful access token response
            //  newTokenRequest --> Returns a new instance of an authorization code token request based on the given code.
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

            //  Creates a new credential for the given user ID based on the given token response and store in the credential store.
            credential = flow.createAndStoreCredential(response, "userID");

            //  Set client's credentials, build
            client = new com.google.api.services.gmail.Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            //  Email address, can be "me" if authorized user
            String userId = "me";
            String searchQuery = "from:(Mail Delivery Subsystem)";

            ListMessagesResponse listMessagesResponse = client.users().messages().list(userId).setQ(searchQuery).execute();

            System.out.println("Found " + listMessagesResponse.getMessages().size() + " bounced emails.");

            for (Message msg : listMessagesResponse.getMessages()) {

                Message message = client.users().messages().get(userId, msg.getId()).execute();
                String body = getEmailBody(message);

                Matcher matcher = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(body);

                if(matcher.find() && !invalidEmailAddresses.containsKey(matcher.group())) {
                    invalidEmailAddresses.put(matcher.group(), message.getId());
                }
            }

            for(String invalidAddress : invalidEmailAddresses.keySet())
            {
//                System.out.println(invalidAddress);
                Invitation invitation = invitationRepository.findFirstByPlayerEmail(invalidAddress);
                if(invitation == null){
                    System.out.println("Invitation null" );
                }
//                invitation.setNoResponse(true);
//                System.out.println("No response: " + invitation.isNoResponse());
//                invitationRepository.save(invitation);
            }

        } catch (Exception e) {

            System.out.println("exception cached ");
            e.printStackTrace();
        }

        return invalidEmailAddresses;
    }

    public HashMap<String, String> BouncedEmailsBody(String code){
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        HashMap<String, String> invalidEmailAddresses = new HashMap<>();

        // String message;
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

            credential = flow.createAndStoreCredential(response, "userID");

            client = new com.google.api.services.gmail.Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            String userId = "me";
            String gmailQuery = "from:(Mail Delivery Subsystem)";

            ListMessagesResponse MsgResponseGmail = client.users().messages().list(userId).setQ(gmailQuery).execute();

            System.out.println("message length:" + MsgResponseGmail.getMessages().size());

            for (Message msg : MsgResponseGmail.getMessages()) {

                Message message = client.users().messages().get(userId, msg.getId()).execute();
                String body = getEmailBody(message);

                Matcher matcher = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(body);

                if(matcher.find() && !invalidEmailAddresses.containsKey(matcher.group())) {
                    invalidEmailAddresses.put(matcher.group(), message.getId());
                    System.out.println("INVALID ADDRESS FOUND: " + matcher.group());
                }
            }

        } catch (Exception e) {
            System.out.println("exception cached ");
            e.printStackTrace();
        }

        return invalidEmailAddresses;
    }

    public String getEmailBody(Message message) throws UnsupportedEncodingException{
        StringBuilder stringBuilder = new StringBuilder();

        getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
        byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
        String text = new String(bodyBytes, "UTF-8");
        return text;
    }

    private void getPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder stringBuilder) {
        for (MessagePart messagePart : messageParts) {
            if (messagePart.getMimeType().equals("text/plain")) {
                stringBuilder.append(messagePart.getBody().getData());
            }

            if (messagePart.getParts() != null) {
                getPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
            }
        }
    }

    public boolean deleteBouncedEmails(String code) {

        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            client = new com.google.api.services.gmail.Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            String userId = "me";
            String query = "from:(Mail Delivery Subsystem)";

            ListMessagesResponse MsgResponse = client.users().messages().list(userId).setQ(query).execute();
            List<Message> messages = new ArrayList<>();

            System.out.println("message length:" + MsgResponse.getMessages().size());

            //DELETES MESSAGES PERMANENTLY. (trash() doesn't)
            for (Message msg : MsgResponse.getMessages()) {
                client.users().messages().trash(userId, msg.getId()).execute();
            }

        } catch (Exception e) {

            System.out.println("exception cached ");
            e.printStackTrace();
        }
        return true;
    }
}



