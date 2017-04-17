package baseline;

import com.google.cloud.pubsub.spi.v1.AckReplyConsumer;
import com.google.cloud.pubsub.spi.v1.MessageReceiver;
import com.google.cloud.pubsub.spi.v1.Subscriber;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import java.io.IOException;

/**
 * A snippet for Google Cloud Pub/Sub showing how to create a Pub/Sub pull subscription and
 * asynchronously pull messages from it.
 */
public class App {

  public static void main(String... args) throws Exception {
    System.out.println("hello...");

    main2(args);


  }

    public static void main2(String... args) throws Exception {
        //  TopicName topic = TopicName.create("fb-user-webhooks", "test-topic");
        SubscriptionName subscription = SubscriptionName.create("fb-user-webhooks", "discover-weekly-fb-images");
//
//    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
//      subscriptionAdminClient
//          .createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 0);
//    }

        MessageReceiver receiver =
            new MessageReceiver() {
                @Override
                public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                    System.out.println("got message: " + message.getData().toStringUtf8());
                    consumer.ack();
                }
            };
        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.defaultBuilder(subscription, receiver).build();
            subscriber.addListener(
                new Subscriber.Listener() {
                    @Override
                    public void failed(Subscriber.State from, Throwable failure) {
                        // Handle failure. This is called when the Subscriber encountered a fatal error and is shutting down.
                        System.err.println(failure);
                    }
                },
                MoreExecutors.directExecutor());
            subscriber.startAsync().awaitRunning();

            Thread.sleep(6000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }
}