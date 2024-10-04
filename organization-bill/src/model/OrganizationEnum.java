package model;

public enum OrganizationEnum {
    WATER("1"),
    ELECTRONIC("2"),
    GAS("3"),
    TELEPHONE("4"),
    MOBILE("5"),
    OTHER("9");

    private  String organizationType;

    OrganizationEnum(String organizationType){
        this.organizationType= organizationType;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public static OrganizationEnum find(String organizationType) {
        for (OrganizationEnum organizationEnum : OrganizationEnum.values()) {
            if(organizationEnum.getOrganizationType().equals(organizationType)) {
                return organizationEnum;
            }
        }

        throw new IllegalArgumentException("Couldn't find a OrganizationEnum for code " + organizationType);
    }

    public static OrganizationEnum findByName(String name) {
        for (OrganizationEnum organizationEnum : OrganizationEnum.values()) {
            if(organizationEnum.name().equalsIgnoreCase(name)) {
                return organizationEnum;
            }
        }

        throw new IllegalArgumentException("Couldn't find a OrganizationEnum for code " + name);
    }
}
