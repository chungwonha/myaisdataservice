package com.chung.example.dbwritingservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AisData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String MMSI;
    private String BaseDateTime;
    private String LAT;
    private String LON;
    private String SOG;
    private String COG;
    private String Heading;
    private String VesselName;
    private String IMO;
    private String CallSign;
    private String VesselType;
    private String Status;
    private String Length;
    private String Width;
    private String Draft;
    private String Cargo;
    private String TranscieverClass;

}
