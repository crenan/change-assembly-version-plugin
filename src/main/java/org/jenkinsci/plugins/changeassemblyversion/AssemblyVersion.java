package org.jenkinsci.plugins.changeassemblyversion;

import hudson.EnvVars;

public class AssemblyVersion {

    private final String version;
    private final String title;
    private final String description;
    private final String configuration;
    private final String company;
    private final String product;
    private final String copyright;
    private final String trademark;
    private final String culture;
    private final String informationalVersion;

    /**
     * The instance of this class gonna return in the property version the value to be used on ChangeTools.
     *
     * @param envVars
     * @param version
     * @param title
     * @param description
     * @param configuration
     * @param company
     * @param product
     * @param copyright
     * @param trademark
     * @param culture
     * @param informationalVersion
     */
    public AssemblyVersion(EnvVars envVars, String version, String title, String description, String configuration,
            String company, String product, String copyright, String trademark, String culture, String informationalVersion) {
        this.version = envVars.expand(version); // Same value for AssemblyFileVersion too
        this.title = envVars.expand(title);
        this.description = envVars.expand(description);
        this.configuration = envVars.expand(configuration);
        this.company = envVars.expand(company);
        this.product = envVars.expand(product);
        this.copyright = envVars.expand(copyright);
        this.trademark = envVars.expand(trademark);
        this.culture = envVars.expand(culture);
        this.informationalVersion = envVars.expand(informationalVersion);
    }

    public String getVersion() {
        return this.version;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getConfiguration() {
        return this.configuration;
    }

    public String getCompany() {
        return this.company;
    }

    public String getProduct() {
        return this.product;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public String getTrademark() {
        return this.trademark;
    }

    public String getCulture() {
        return this.culture;
    }

    public String getInformationalVersion() {
        return this.informationalVersion;
    }
}
