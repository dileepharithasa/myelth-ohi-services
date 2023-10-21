package com.myelth.ohi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonDomain implements Serializable {

    private Integer id;
    private Integer objectVersionNumber;
    private List<Link> links;
}
