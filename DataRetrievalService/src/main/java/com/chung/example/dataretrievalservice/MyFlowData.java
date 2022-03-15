package com.chung.example.dataretrievalservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyFlowData implements java.io.Serializable {

    private String firstName;
    private String lastName;
}
