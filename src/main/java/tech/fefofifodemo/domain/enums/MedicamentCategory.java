package tech.fefofifodemo.domain.enums;

public enum MedicamentCategory {
    CONTROLLED_SUBSTANCE,
    VACCINE,
    BLOOD_DERIVATIVE,
    BIOTECHNOLOGICAL,
    INSULIN,
    HOMEOPATHIC,
    ANTIBIOTIC,
    ANTIVIRAL,
    ANTIFUNGAL,
    ANALGESIC,
    ANTIPYRETIC,
    ANTIHYPERTENSIVE,
    ANTIDIABETIC,
    ANTI_INFLAMMATORY,
    OTHER;

    public boolean requiresFEFO(boolean coldChain) {
        return this == CONTROLLED_SUBSTANCE
                || this == VACCINE
                || this == BLOOD_DERIVATIVE
                || this == BIOTECHNOLOGICAL
                || this == INSULIN
                || this == HOMEOPATHIC
                || coldChain;
    }
}
