package uk.gov.ons.census.action.model.dto;

import lombok.Data;

@Data
public class CollectionCase {
  private String id;
  private String caseRef;
  private String survey;
  private String collectionExerciseId;
  private Address address;
  private String state;
  private String actionableFrom;
  private String actionPlanId;
  private String treatmentCode;
}
