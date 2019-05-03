package uk.gov.ons.census.action.messaging;

import java.util.UUID;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.action.model.dto.CollectionCase;
import uk.gov.ons.census.action.model.dto.EventType;
import uk.gov.ons.census.action.model.dto.ResponseManagementEvent;
import uk.gov.ons.census.action.model.dto.Uac;
import uk.gov.ons.census.action.model.entity.Case;
import uk.gov.ons.census.action.model.entity.CaseState;
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
  public void receiveEvent(ResponseManagementEvent responseManagementEvent) {
    if (responseManagementEvent.getEvent().getType() == EventType.CASE_CREATED) {
      processCaseCreatedEvent(responseManagementEvent.getPayload().getCollectionCase());
    } else if (responseManagementEvent.getEvent().getType() == EventType.UAC_UPDATED) {
      processUacUpdated(responseManagementEvent.getPayload().getUac());
    } else {
      // This code can't be reached because under the class structure the EventType is limited to
      // enums at this point?
      throw new RuntimeException(); // Unexpected event type - maybe throw away?
    }
  }

  private void processCaseCreatedEvent(CollectionCase collectionCase) {
    Case newCase = new Case();
    newCase.setCaseRef(Long.parseLong(collectionCase.getCaseRef()));
    newCase.setCaseId(UUID.fromString(collectionCase.getId()));
    newCase.setState(CaseState.valueOf(collectionCase.getState()));
    newCase.setAddressLine1(collectionCase.getAddress().getAddressLine1());
    newCase.setAddressLine2(collectionCase.getAddress().getAddressLine2());
    newCase.setAddressLine3(collectionCase.getAddress().getAddressLine3());
    newCase.setTownName(collectionCase.getAddress().getTownName());
    newCase.setPostcode(collectionCase.getAddress().getPostcode());
    newCase.setArid(collectionCase.getAddress().getArid());
    newCase.setLatitude(collectionCase.getAddress().getLatitude());
    newCase.setLongitude(collectionCase.getAddress().getLongitude());
    newCase.setUprn(collectionCase.getAddress().getUprn());
    newCase.setRgn(collectionCase.getAddress().getRegion());

    // Below this line is extra data potentially needed by Action Scheduler - can be ignored by RM
    newCase.setActionPlanId(collectionCase.getActionPlanId()); // This is essential
    newCase.setTreatmentCode(collectionCase.getTreatmentCode()); // This is essential
    newCase.setOa(collectionCase.getOa());
    newCase.setLsoa(collectionCase.getLsoa());
    newCase.setMsoa(collectionCase.getMsoa());
    newCase.setLad(collectionCase.getLad());
    newCase.setHtcWillingness(collectionCase.getHtcWillingness());
    newCase.setHtcDigital(collectionCase.getHtcDigital());

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
