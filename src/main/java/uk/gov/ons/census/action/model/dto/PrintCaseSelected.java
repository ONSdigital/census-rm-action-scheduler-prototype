package uk.gov.ons.census.action.model.dto;

import lombok.Data;

@Data
public class PrintCaseSelected {
  private int caseRef;
  private String packCode;
  private String actionRuleId;
}
