package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for storing the type of frontend component used to fill in an ActivityResult
 */
public enum OutcomeInputType {

    // simple string response
    TEXT {
        public OutcomeDataType dataType() {return OutcomeDataType.STRING;}
    },

    // Used for boolean input
    CHECKBOX {
        public OutcomeDataType dataType() {return OutcomeDataType.BOOLEAN;}
    },

    // Multi-choice where only one option is selected
    MULTICHOICE_SINGLE {
        public OutcomeDataType dataType() {return OutcomeDataType.INTEGER;}
    },

    // Multi-choice where any number of options can be selected - represented by checkboxes
    MULTICHOICE_COMBINATION {
        public OutcomeDataType dataType() {return OutcomeDataType.BOOLEAN_ARRAY;}
    },

    // Time includes hours and minutes
    TIME {
        public OutcomeDataType dataType() {return OutcomeDataType.TIME;}
    },

    // Calendar date
    DATE {
        public OutcomeDataType dataType() {return OutcomeDataType.DATE;}
    },

    // Time includes hours and minutes
    INTEGER {
        public OutcomeDataType dataType() {return OutcomeDataType.INTEGER;}
    },

    // Calendar date
    FLOAT {
        public OutcomeDataType dataType() {return OutcomeDataType.FLOAT;}
    };


    @JsonValue
    public String getText() {
        return this.toString();
    }

    @Override
    public String toString() { return name().toLowerCase(); }

}
