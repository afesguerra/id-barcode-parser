package com.afesguerra.colombia.id.barcode;

import com.afesguerra.colombia.id.barcode.model.Gender;
import com.afesguerra.colombia.id.barcode.model.IDData;
import com.afesguerra.colombia.id.barcode.model.bloodtype.ABOGroup;
import com.afesguerra.colombia.id.barcode.model.bloodtype.BloodType;
import com.afesguerra.colombia.id.barcode.model.bloodtype.RhFactor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IDReader {
    public IDData readIDData(String data) {
        final IDV3Parser parser = new IDV3Parser(data);

        final IDData.IDDataBuilder idDataBuilder = IDData.builder();

        idDataBuilder.idNumber(parser.getIdNumber());
        idDataBuilder.givenName(parser.getGivenName());
        idDataBuilder.lastName(parser.getLastName());
        idDataBuilder.gender(parser.getGender());
        idDataBuilder.birthdate(parser.getBirthdate());
        idDataBuilder.bloodType(parser.getBloodType());

        return idDataBuilder.build();
    }

    private static class IDV3Parser {
        private static final DateTimeFormatter BIRTHDATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

        private final String data;

        public IDV3Parser(String data) {
            this.data = data;
        }

        public long getIdNumber() {
            final String rawValue = getRawValue(Field.ID_NUMBER);
            return Long.parseLong(rawValue);
        }

        public String getGivenName() {
            final String rawValue = getRawValue(Field.GIVEN_NAME);
            return String.join(" ", rawValue.split(getDelimiter() + "+")).trim();
        }

        public String getLastName() {
            final String rawValue = getRawValue(Field.LAST_NAME);
            return String.join(" ", rawValue.split(getDelimiter() + "+")).trim();
        }

        public Gender getGender() {
            final String gender = getRawValue(Field.GENDER);
            return "F".equals(gender) ? Gender.FEMALE : Gender.MALE;
        }

        public LocalDate getBirthdate() {
            final String rawValue = getRawValue(Field.BIRTHDATE);
            return LocalDate.parse(rawValue, BIRTHDATE_FORMATTER);
        }

        public BloodType getBloodType() {
            final String rawValue = getRawValue(Field.BLOOD_TYPE);
            // TODO Add support for AB group
            final ABOGroup aboGroup = ABOGroup.valueOf(rawValue.substring(0, 1));
            final RhFactor rhFactor = rawValue.substring(1, 2).equals("+") ? RhFactor.POSITIVE : RhFactor.NEGATIVE;
            return new BloodType(aboGroup, rhFactor);
        }

        private String getDelimiter() {
            final String version = data.substring(0, 2);
            switch (version) {
                case "02":
                    return " ";
                case "03":
                    return Character.toString((char) 0x00);
                default:
                    throw new RuntimeException("Invalid Version");
            }
        }

        private enum Field {
            ID_NUMBER(48, 10),
            LAST_NAME(58, 46),
            GIVEN_NAME(104, 46),
            GENDER(151, 1),
            BIRTHDATE(152, 8),
            BLOOD_TYPE(166, 2);

            private final int offset;
            private final int lenght;

            Field(int offset, int lenght) {
                this.offset = offset;
                this.lenght = lenght;
            }
        }

        private String getRawValue(final Field field) {
            return data.substring(field.offset, field.offset + field.lenght);
        }
    }
}
