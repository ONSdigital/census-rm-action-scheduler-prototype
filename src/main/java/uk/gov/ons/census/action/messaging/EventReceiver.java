package uk.gov.ons.census.action.messaging;

import java.util.List;
import java.util.UUID;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.action.model.dto.CollectionCase;
import uk.gov.ons.census.action.model.dto.EventType;
import uk.gov.ons.census.action.model.dto.FanoutEvent;
import uk.gov.ons.census.action.model.dto.Uac;
import uk.gov.ons.census.action.model.entity.Case;
import uk.gov.ons.census.action.model.entity.UacQidLink;
import uk.gov.ons.census.action.model.repository.CaseRepository;
import uk.gov.ons.census.action.model.repository.UacQidLinkRepository;

@MessageEndpoint
public class EventReceiver {
  private CaseRepository caseRepository;
  private UacQidLinkRepository uacQidLinkRepository;

  public EventReceiver(CaseRepository caseRepository, UacQidLinkRepository uacQidLinkRepository) {
    this.caseRepository = caseRepository;
    this.uacQidLinkRepository = uacQidLinkRepository;
  }

  @Transactional
  @ServiceActivator(inputChannel = "caseCreatedInputChannel")
  public void receiveEvent(FanoutEvent fanoutEvent) {
    if (fanoutEvent.getEvent().getType() == EventType.CASE_CREATED) {
      processCaseCreatedEvent(fanoutEvent.getPayload().getCollectionCase());
    } else if (fanoutEvent.getEvent().getType() == EventType.UAC_UPDATED) {
      processUacUpdated(fanoutEvent.getPayload().getUac());
    } else {
      throw new RuntimeException(); // Unexpected event type - maybe throw away?
    }
  }

  private void processCaseCreatedEvent(CollectionCase collectionCase) {
    Case newCase = new Case();
    newCase.setCaseRef(Long.parseLong(collectionCase.getCaseRef()));
    newCase.setCaseId(UUID.fromString(collectionCase.getId()));
    newCase.setActionPlanId(collectionCase.getActionPlanId());
    newCase.setTreatmentCode(collectionCase.getTreatmentCode());
    newCase.setAddressLine1(collectionCase.getAddress().getAddressLine1());
    newCase.setAddressLine2(collectionCase.getAddress().getAddressLine2());
    newCase.setAddressLine3(collectionCase.getAddress().getAddressLine3());
    newCase.setTownName(collectionCase.getAddress().getTownName());
    newCase.setPostcode(collectionCase.getAddress().getPostcode());
    caseRepository.save(newCase);
  }

  private void processUacUpdated(Uac uac) {
    UacQidLink uacQidLink = new UacQidLink();
    uacQidLink.setId(UUID.randomUUID());
    uacQidLink.setQid(uac.getQuestionnaireId());
    uacQidLink.setUac(uac.getUac());
    uacQidLink.setCaseId(uac.getCaseId());
    uacQidLinkRepository.save(uacQidLink);
  }
}
