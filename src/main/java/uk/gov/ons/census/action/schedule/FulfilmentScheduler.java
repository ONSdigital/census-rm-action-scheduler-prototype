package uk.gov.ons.census.action.schedule;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FulfilmentScheduler {
  private final FulfilmentProcessor fulfilmentProcessor;
  private static final Logger log = LoggerFactory.getLogger(ActionRuleScheduler.class);

  public FulfilmentScheduler(FulfilmentProcessor fulfilmentProcessor) {
    this.fulfilmentProcessor = fulfilmentProcessor;
  }

  @Scheduled(cron = "${schedule.time}")
  public void TriggerFulfilments() {
    try {
      fulfilmentProcessor.addFulfilmentBatchIdAndQuantity();
    } catch (Exception e) {
      log.error("Unexpected exception while processing fulfilments", e);
    }
  }
}
