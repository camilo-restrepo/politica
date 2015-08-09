import java.io.IOException;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class JavaCustomReceiver extends Receiver<String> {

	public JavaCustomReceiver() {
		super(StorageLevel.MEMORY_AND_DISK_2());
	}

	public void onStart() {
		new Thread() {
			@Override
			public void run() {
				receive();
			}
		}.start();
	}

	public void onStop() {
		// There is nothing much to do as the thread calling receive()
		// is designed to stop by itself if isStopped() returns false
	}

	private void receive() {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("192.168.0.15");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare("wordCount", false, false, false, null);

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String in = new String(body);
					store(in);
				}
			};
			channel.basicConsume("wordCount", true, consumer);
		} catch (Exception e) {

		}
	}
}