package se.citerus.dddsample.infrastructure.messaging.jms;

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

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

@EnableJms
@Configuration
public class InfrastructureMessagingJmsConfig {

    @Value("${brokerUrl}")
    private String brokerUrl;

    @Bean
    public SimpleLoggingConsumer simpleLoggingConsumer() {
        return new SimpleLoggingConsumer();
    }

    @Bean
    public CargoHandledConsumer cargoHandledConsumer(CargoInspectionService cargoInspectionService) {
        return new CargoHandledConsumer(cargoInspectionService);
    }

    @Bean
    public HandlingEventRegistrationAttemptConsumer handlingEventRegistrationAttemptConsumer(HandlingEventService handlingEventService) {
        return new HandlingEventRegistrationAttemptConsumer(handlingEventService);
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
        return new ActiveMQConnectionFactory(brokerUrl);
    }

    @Bean
    public JmsOperations jmsOperations(ConnectionFactory jmsConnectionFactory) {
        return new JmsTemplate(jmsConnectionFactory);
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
