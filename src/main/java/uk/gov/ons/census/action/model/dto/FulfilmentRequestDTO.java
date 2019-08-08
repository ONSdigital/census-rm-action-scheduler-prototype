package uk.gov.ons.census.action.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FulfilmentRequestDTO {

  @JsonInclude(Include.NON_NULL)
  private String caseId;

  private String fulfilmentCode;

  private Contact contact;
}
