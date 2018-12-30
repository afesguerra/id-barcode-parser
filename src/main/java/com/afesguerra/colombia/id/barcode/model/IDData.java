package com.afesguerra.colombia.id.barcode.model;

import com.afesguerra.colombia.id.barcode.model.bloodtype.BloodType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class IDData {
    private final long idNumber;
    private final String givenName;
    private final String lastName;
    private final Gender gender;
    private final LocalDate birthdate;
    private final BloodType bloodType;
}
