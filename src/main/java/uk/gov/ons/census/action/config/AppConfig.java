package uk.gov.ons.census.action.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class AppConfig {
  @Value("${queueconfig.inbound-queue}")
  private String inboundQueue;

  @Value("${queueconfig.consumers}")
  private int consumers;

  @Bean
  public MessageChannel caseCreatedInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public AmqpInboundChannelAdapter inbound(
      SimpleMessageListenerContainer listenerContainer,
      @Qualifier("caseCreatedInputChannel") MessageChannel channel) {
    AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
    adapter.setOutputChannel(channel);
    return adapter;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    rabbitTemplate.setChannelTransacted(true);
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
    SimpleMessageListenerContainer container =
        new SimpleMessageListenerContainer(connectionFactory);
    container.setQueueNames(inboundQueue);
    container.setConcurrentConsumers(consumers);
    return container;
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    return rabbitAdmin;
  }

  @Bean
  public Jaxb2Marshaller actionInstructionFieldMarshaller() {
    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath("uk.gov.ons.census.action.model.dto.instruction.field");
    return jaxb2Marshaller;
  }

  @Bean
  public MarshallingMessageConverter actionInstructionFieldMarshallingMessageConverter(
      Jaxb2Marshaller actionInstructionFieldMarshaller) {
    MarshallingMessageConverter marshallingMessageConverter =
        new MarshallingMessageConverter(actionInstructionFieldMarshaller);
    marshallingMessageConverter.setContentType("text/xml");
    return marshallingMessageConverter;
  }

  @Bean
  public RabbitTemplate actionInstructionFieldRabbitTemplate(
      ConnectionFactory connectionFactory,
      MarshallingMessageConverter actionInstructionFieldMarshallingMessageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(actionInstructionFieldMarshallingMessageConverter);
    rabbitTemplate.setChannelTransacted(true);
    return rabbitTemplate;
  }



  @Bean
  public Jaxb2Marshaller actionInstructionPrinterMarshaller() {
    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath("uk.gov.ons.census.action.model.dto.instruction.printer");
    return jaxb2Marshaller;
  }

  @Bean
  public MarshallingMessageConverter actionInstructionPrinterMarshallingMessageConverter(
      Jaxb2Marshaller actionInstructionPrinterMarshaller) {
    MarshallingMessageConverter marshallingMessageConverter =
        new MarshallingMessageConverter(actionInstructionPrinterMarshaller);
    marshallingMessageConverter.setContentType("text/xml");
    return marshallingMessageConverter;
  }

  @Bean
  public RabbitTemplate actionInstructionPrinterRabbitTemplate(
      ConnectionFactory connectionFactory,
      MarshallingMessageConverter actionInstructionPrinterMarshallingMessageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(actionInstructionPrinterMarshallingMessageConverter);
    rabbitTemplate.setChannelTransacted(true);
    return rabbitTemplate;
  }
}
