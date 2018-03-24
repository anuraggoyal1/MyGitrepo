package com.anurag.stocks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.constants.Constants;
import de.bytefish.fcmjava.http.options.IFcmClientSettings;
import de.bytefish.fcmjava.model.enums.PriorityEnum;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.model.topics.Topic;
import de.bytefish.fcmjava.requests.topic.TopicUnicastMessage;
import de.bytefish.fcmjava.responses.TopicMessageResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.time.Duration;

/**
 * Created by gpq5 on 21/12/17.
 */
public class Test {

    public static void main(String[] args) throws Exception {


        PersonData p = new PersonData("Philipp", "Wagner");

        FCMHelper instance = FCMHelper.getInstance();

        JsonObject notificationObject = new JsonObject();
        notificationObject.addProperty("Test", "data"); // See GSON-Reference

        instance.sendTopicNotificationAndData("stockAlert", notificationObject, notificationObject);
        // System.out.println(counter);


        IFcmClientSettings clientSettings = new FixedFcmClientSettings("AAAAyvSEzN0:APA91bGE7hjwXFljEF92H6gis-BIo15bxtvwzSKuVwBOFxbszg8XNHA3YrWqZsYgTjRa8xdqXTwlppXuvMSs-34X9iB3D9BeD0Qac7cmhhE1VNbXUCJSlDdHuk0-zJU61OFJSUQlpVfr");

        // Create the Client using system-properties-based settings:
        FcmClient client = new FcmClient(clientSettings);

            // Message Options:
            FcmMessageOptions options = FcmMessageOptions.builder()
                    .setTimeToLive(Duration.ofHours(1)).setPriorityEnum(PriorityEnum.High)
                    .build();

            // Send a Message:
            TopicMessageResponse response = client.send(new TopicUnicastMessage(options, new Topic("stockAlert"), p));

     //   Topiccclient.send(new NotificationUnicastMessage());

    }

    /**
     * Created by gpq5 on 24/12/17.
     */
    public static class PersonData {
        private final String firstName;
        private final String lastName;

        public PersonData(String firstName, String lastName) {


            this.firstName = firstName;
            this.lastName = lastName;
        }

        @JsonProperty("firstName")
        public String getFirstName() {
            return firstName;
        }

        @JsonProperty("lastName")
        public String getLastName() {
            return lastName;
        }
    }

    /** Created by Toni on 30.09.2016. */
    public static class FCMHelper {

      /** Instance */
      private static FCMHelper instance = null;

      /** Google URL to use firebase cloud messenging */
      private static final String URL_SEND = "https://fcm.googleapis.com/fcm/send";

      /** STATIC TYPES */
      public static final String TYPE_TO = "to"; // Use for single devices, device groups and topics

      public static final String TYPE_CONDITION = "condition"; // Use for Conditions

      /** Your SECRET server key */
      private static final String FCM_SERVER_KEY = "AAAAyvSEzN0:APA91bGE7hjwXFljEF92H6gis-BIo15bxtvwzSKuVwBOFxbszg8XNHA3YrWqZsYgTjRa8xdqXTwlppXuvMSs-34X9iB3D9BeD0Qac7cmhhE1VNbXUCJSlDdHuk0-zJU61OFJSUQlpVfr";

      public static FCMHelper getInstance() {
        if (instance == null) instance = new FCMHelper();
        return instance;
      }

      private FCMHelper() {}

      /**
       * Send notification
       *
       * @param type
       * @param typeParameter
       * @param notificationObject
       * @return
       * @throws IOException
       */
      public String sendNotification(String type, String typeParameter, JsonObject notificationObject)
          throws IOException {
        return sendNotifictaionAndData(type, typeParameter, notificationObject, null);
      }

      /**
       * Send data
       *
       * @param type
       * @param typeParameter
       * @param dataObject
       * @return
       * @throws IOException
       */
      public String sendData(String type, String typeParameter, JsonObject dataObject)
          throws IOException {
        return sendNotifictaionAndData(type, typeParameter, null, dataObject);
      }

      /**
       * Send notification and data
       *
       * @param type
       * @param typeParameter
       * @param notificationObject
       * @param dataObject
       * @return
       * @throws IOException
       */
      public String sendNotifictaionAndData(
          String type, String typeParameter, JsonObject notificationObject, JsonObject dataObject)
          throws IOException {
        String result = null;
        if (type.equals(TYPE_TO) || type.equals(TYPE_CONDITION)) {
          JsonObject sendObject = new JsonObject();
          sendObject.addProperty(type, typeParameter);
          result = sendFcmMessage(sendObject, notificationObject, dataObject);
        }
        return result;
      }

      /**
       * Send data to a topic
       *
       * @param topic
       * @param dataObject
       * @return
       * @throws IOException
       */
      public String sendTopicData(String topic, JsonObject dataObject) throws IOException {
        return sendData(TYPE_TO, "/topics/" + topic, dataObject);
      }

      /**
       * Send notification to a topic
       *
       * @param topic
       * @param notificationObject
       * @return
       * @throws IOException
       */
      public String sendTopicNotification(String topic, JsonObject notificationObject)
          throws IOException {
        return sendNotification(TYPE_TO, "/topics/" + topic, notificationObject);
      }

      /**
       * Send notification and data to a topic
       *
       * @param topic
       * @param notificationObject
       * @param dataObject
       * @return
       * @throws IOException
       */
      public String sendTopicNotificationAndData(
          String topic, JsonObject notificationObject, JsonObject dataObject) throws IOException {
        return sendNotifictaionAndData(TYPE_TO, "/topics/" + topic, notificationObject, dataObject);
      }

      /**
       * Send a Firebase Cloud Message
       *
       * @param sendObject - Contains to or condition
       * @param notificationObject - Notification Data
       * @param dataObject - Data
       * @return
       * @throws IOException
       */
      private String sendFcmMessage(
          JsonObject sendObject, JsonObject notificationObject, JsonObject dataObject)
          throws IOException {
        HttpPost httpPost = new HttpPost(URL_SEND);

        // Header setzen
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "key=" + FCM_SERVER_KEY);

        if (notificationObject != null) sendObject.add("notification", notificationObject);
        if (dataObject != null) sendObject.add("data", dataObject);

        String data = sendObject.toString();

        StringEntity entity = new StringEntity(data);

        // JSON-Object übergeben
        httpPost.setEntity(entity);

        HttpClient httpClient = HttpClientBuilder.create().build();

        BasicResponseHandler responseHandler = new BasicResponseHandler();
        String response = (String) httpClient.execute(httpPost, responseHandler);

        return response;
      }
    }

    /** Created by gpq5 on 24/12/17. */
    public static class FixedFcmClientSettings implements IFcmClientSettings {

      private final String apiKey;

      public FixedFcmClientSettings(String apiKey) {
        this.apiKey = apiKey;
      }

      @Override
      public String getFcmUrl() {
        return Constants.FCM_URL;
      }

      @Override
      public String getApiKey() {
        return apiKey;
      }
    }
}
