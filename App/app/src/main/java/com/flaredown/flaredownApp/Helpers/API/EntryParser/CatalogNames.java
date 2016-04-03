package com.flaredown.flaredownApp.Helpers.API.EntryParser;

/**
 * Created by thunter on 28/02/16.
 */
public enum CatalogNames {
    SYMPTOMS ("symptoms"),
    CONDITIONS ("conditions"),
    TREATMENTS ("treatments"),
    SPECIALISED ("specialised");

    private final String name;

    CatalogNames(String s) {
        name = s;
    }

    /**
     * If enum is equal to it matching name.
     * @param otherName Name to compare with.
     * @return True if equal.
     */
    public boolean equalsName(String otherName) {
        return (otherName != null && name.equals(otherName));
    }

    public String getName() {
        return name;
    }

    public static CatalogNames toEnum(String key) {
        CatalogNames catalogNames[] = CatalogNames.values();
        for (CatalogNames catalogName : catalogNames) {
            if(catalogName.equalsName(key))
                return catalogName;
        }
        return CatalogNames.SPECIALISED;
    }

    public static String[] stringValues() {
        String returnS[] = new String[CatalogNames.values().length];
        for (int i = 0; i < CatalogNames.values().length; i++) {
            returnS[i] = CatalogNames.values()[i].getName();
        }
        return returnS;
    }
}
