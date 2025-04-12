package se.citerus.dddsample.infrastructure.messaging.jms;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.application.HandlingEventService;

import java.util.List;

@EnableJms
@Configuration
public class InfrastructureMessagingJmsConfig {

    @Value("${brokerUrl}")
    private String brokerUrl;

    @Bean(value = "cargoHandledConsumer", destroyMethod = "close")
    public MessageConsumer cargoHandledConsumer(Session session, @Qualifier("cargoHandledQueue") Destination destination, CargoInspectionService cargoInspectionService) throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new CargoHandledConsumer(cargoInspectionService));
        return consumer;
    }

    @Bean(value = "handlingEventRegistrationAttemptConsumer", destroyMethod = "close")
    public MessageConsumer handlingEventRegistrationAttemptConsumer(Session session, @Qualifier("handlingEventRegistrationAttemptQueue") Destination destination, HandlingEventService handlingEventService) throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new HandlingEventRegistrationAttemptConsumer(handlingEventService));
        return consumer;
    }

    @Bean(value = "misdirectedCargoConsumer", destroyMethod = "close")
    public MessageConsumer misdirectedCargoConsumer(Session session, @Qualifier("misdirectedCargoQueue") Destination destination) throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new SimpleLoggingConsumer());
        return consumer;
    }

    @Bean(value = "deliveredCargoConsumer", destroyMethod = "close")
    public MessageConsumer deliveredCargoConsumer(Session session, @Qualifier("deliveredCargoQueue") Destination destination) throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new SimpleLoggingConsumer());
        return consumer;
    }

    @Bean(value = "rejectedRegistrationAttemptsConsumer", destroyMethod = "close")
    public MessageConsumer rejectedRegistrationAttemptsConsumer(Session session, @Qualifier("rejectedRegistrationAttemptsQueue") Destination destination) throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new SimpleLoggingConsumer());
        return consumer;
    }

    @Bean("cargoHandledQueue")
    public Destination cargoHandledQueue() throws Exception {
        return createQueue("CargoHandledQueue");
    }

    @Bean("misdirectedCargoQueue")
    public Destination misdirectedCargoQueue() throws Exception {
        return createQueue("MisdirectedCargoQueue");
    }

    @Bean("deliveredCargoQueue")
    public Destination deliveredCargoQueue() throws Exception {
        return createQueue("DeliveredCargoQueue");
    }

    @Bean("handlingEventRegistrationAttemptQueue")
    public Destination handlingEventRegistrationAttemptQueue() throws Exception {
        return createQueue("HandlingEventRegistrationAttemptQueue");
    }

    @Bean("rejectedRegistrationAttemptsQueue")
    public Destination rejectedRegistrationAttemptsQueue() throws Exception {
        return createQueue("RejectedRegistrationAttemptsQueue");
    }

    @Bean
    public DefaultJmsListenerContainerFactory listenerContainerFactory(ConnectionFactory jmsConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory);
        factory.setConcurrency("1-1");
        return factory;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setTrustedPackages(List.of("se.citerus.dddsample.interfaces.handling", "se.citerus.dddsample.domain", "java.util"));
        return factory;
    }

    @Bean
    public JmsOperations jmsOperations(ConnectionFactory jmsConnectionFactory) {
        return new JmsTemplate(jmsConnectionFactory);
    }

    @Bean(destroyMethod = "close")
    public Connection connection(ConnectionFactory connectionFactory) throws JMSException {
        QueueConnection queueConnection = ((ActiveMQConnectionFactory) connectionFactory).createQueueConnection();
        queueConnection.start();
        return queueConnection;
    }

    @Bean
    public Session session(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Bean
    public ApplicationEvents applicationEvents(JmsOperations jmsOperations, @Qualifier("cargoHandledQueue") Destination cargoHandledQueue,
                                               @Qualifier("misdirectedCargoQueue") Destination misdirectedCargoQueue, @Qualifier("deliveredCargoQueue") Destination deliveredCargoQueue,
                                               @Qualifier("rejectedRegistrationAttemptsQueue") Destination rejectedRegistrationAttemptsQueue, @Qualifier("handlingEventRegistrationAttemptQueue") Destination handlingEventRegistrationAttemptQueue) {
        return new JmsApplicationEventsImpl(jmsOperations, cargoHandledQueue, misdirectedCargoQueue, deliveredCargoQueue, rejectedRegistrationAttemptsQueue, handlingEventRegistrationAttemptQueue);
    }

    private Destination createQueue(String queueName) {
        return ActiveMQDestination.createDestination(queueName, ActiveMQDestination.QUEUE_TYPE);
    }
}
