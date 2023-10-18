package com.myelth.ohi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenderingAddress   {
  private String id;
  private String objectVersionNumber;
  private ServiceAddress serviceAddress;
  private String startDate;
  private String endDate;


}

