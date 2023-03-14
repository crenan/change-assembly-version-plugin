package org.jenkinsci.plugins.changeassemblyversion;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author <a href="mailto:leonardo.kobus@hbsis.com.br">Leonardo Kobus</a>
 */
public class ChangeAssemblyVersion extends Builder implements SimpleBuildStep {

    private String assemblyFile;
    private String regexPattern;
    private String replacementPattern;
    private String assemblyTitle;
    private String assemblyDescription;
    private String assemblyConfiguration;
    private String assemblyCompany;
    private String assemblyProduct;
    private String assemblyCopyright;
    private String assemblyTrademark;
    private String assemblyCulture;
    private String assemblyVersion;
    private String assemblyInformationalVersion;

    @Deprecated
    public ChangeAssemblyVersion(String assemblyFile, String regexPattern, String replacementPattern,
            String assemblyTitle, String assemblyDescription, String assemblyConfiguration, String assemblyCompany,
            String assemblyProduct, String assemblyCopyright, String assemblyTrademark, String assemblyCulture,
            String assemblyVersion, String assemblyInformationalVersion) {
        this.assemblyFile = assemblyFile;
        this.regexPattern = regexPattern;
        this.replacementPattern = replacementPattern;
        this.assemblyTitle = assemblyTitle;
        this.assemblyDescription = assemblyDescription;
        this.assemblyConfiguration = assemblyConfiguration;
        this.assemblyCompany = assemblyCompany;
        this.assemblyProduct = assemblyProduct;
        this.assemblyCopyright = assemblyCopyright;
        this.assemblyTrademark = assemblyTrademark;
        this.assemblyCulture = assemblyCulture;
        this.assemblyVersion = assemblyVersion;
        this.assemblyInformationalVersion = assemblyInformationalVersion;
    }

    @DataBoundConstructor
    public ChangeAssemblyVersion(String assemblyVersion) {
        this.assemblyVersion = assemblyVersion;
    }

    @DataBoundSetter
    public void setAssemblyFile(String file) {
        this.assemblyFile = file;
    }

    @DataBoundSetter
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    @DataBoundSetter
    public void setReplacementPattern(String pattern) {
        this.replacementPattern = pattern;
    }

    @DataBoundSetter
    public void setAssemblyTitle(String title) {
        this.assemblyTitle = title;
    }

    @DataBoundSetter
    public void setAssemblyDescription(String description) {
        this.assemblyDescription = description;
    }

    @DataBoundSetter
    public void setAssemblyConfiguration(String configuration) {
        this.assemblyConfiguration = configuration;
    }

    @DataBoundSetter
    public void setAssemblyCompany(String company) {
        this.assemblyCompany = company;
    }

    @DataBoundSetter
    public void setAssemblyProduct(String product) {
        this.assemblyProduct = product;
    }

    @DataBoundSetter
    public void setAssemblyCopyright(String copyright) {
        this.assemblyCopyright = copyright;
    }

    @DataBoundSetter
    public void setAssemblyTrademark(String trademark) {
        this.assemblyTrademark = trademark;
    }

    @DataBoundSetter
    public void setAssemblyCulture(String culture) {
        this.assemblyCulture = culture;
    }

    @DataBoundSetter
    public void setAssemblyInformationalVersion(String informationalVersion) {
        this.assemblyInformationalVersion = informationalVersion;
    }

    public String getAssemblyFile() {
        return this.assemblyFile;
    }

    public String getRegexPattern() {
        return this.regexPattern;
    }

    public String getReplacementPattern() {
        return this.replacementPattern;
    }

    public String getAssemblyTitle() {
        return this.assemblyTitle;
    }

    public String getAssemblyDescription() {
        return this.assemblyDescription;
    }

    public String getAssemblyConfiguration() {
        return this.assemblyConfiguration;
    }

    public String getAssemblyCompany() {
        return this.assemblyCompany;
    }

    public String getAssemblyProduct() {
        return this.assemblyProduct;
    }

    public String getAssemblyCopyright() {
        return this.assemblyCopyright;
    }

    public String getAssemblyTrademark() {
        return this.assemblyTrademark;
    }

    public String getAssemblyCulture() {
        return this.assemblyCulture;
    }

    public String getAssemblyVersion() {
        return this.assemblyVersion;
    }

    public String getAssemblyInformationalVersion() {
        return this.assemblyInformationalVersion;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        try {
            perform(build, build.getWorkspace(), launcher, listener);
        } catch (AbortException ex) {
            return false;
        } catch (IOException | InterruptedException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            listener.getLogger().println(sw.toString());
            throw ex;
        }
        return true;
    }

    /**
     *
     * The perform method is gonna search all the file named "Assemblyinfo.cs" in any folder below,
     * and after found will change the version of AssemblyVersion and AssemblyFileVersion in the file
     * for the inserted version (versionPattern property value).OBS: The inserted value can be some jenkins
     * variable like ${BUILD_NUMBER} just the variable alone, but not implemented to treat 0.0.${BUILD_NUMBER}.0
     * I think this plugin must be used with Version Number Plugin.
     *
     * @param run
     * @param workspace
     * @param launcher
     * @param listener
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws hudson.AbortException
     */
    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException, AbortException {

        try {
            String assemblyGlob = this.assemblyFile == null || this.assemblyFile.equals("") ? "**/AssemblyInfo.cs" : this.assemblyFile;

            EnvVars envVars = run.getEnvironment(listener);

            if (run instanceof AbstractBuild) {
                envVars.overrideAll(((AbstractBuild<?, ?>) run).getBuildVariables());
            }

            AssemblyVersion assemblyInformation = new AssemblyVersion(envVars,
                    this.assemblyVersion, this.assemblyTitle, this.assemblyDescription, this.assemblyConfiguration,
                    this.assemblyCompany, this.assemblyProduct, this.assemblyCopyright, this.assemblyTrademark,
                    this.assemblyCulture, this.assemblyInformationalVersion);

            String version = assemblyInformation.getVersion();
            if (this.assemblyVersion == null || StringUtils.isEmpty(this.assemblyVersion)) {
                listener.getLogger().println("Please provide a valid version pattern.");
                throw new AbortException("Please provide a valid version pattern.");
            }

            // Log new expanded values
            listener.getLogger().println(String.format("Changing File(s): %s", assemblyGlob));
            listener.getLogger().println(String.format("Assembly Title : %s", assemblyInformation.getTitle()));
            listener.getLogger().println(String.format("Assembly Description : %s", assemblyInformation.getDescription()));
            listener.getLogger().println(String.format("Assembly Configuration : %s", assemblyInformation.getConfiguration()));
            listener.getLogger().println(String.format("Assembly Company : %s", assemblyInformation.getCompany()));
            listener.getLogger().println(String.format("Assembly Product : %s", assemblyInformation.getProduct()));
            listener.getLogger().println(String.format("Assembly Copyright : %s", assemblyInformation.getCopyright()));
            listener.getLogger().println(String.format("Assembly Trademark : %s", assemblyInformation.getTrademark()));
            listener.getLogger().println(String.format("Assembly Culture : %s", assemblyInformation.getCulture()));
            listener.getLogger().println(String.format("Assembly Version : %s", assemblyInformation.getVersion()));
            listener.getLogger().println(String.format("Assembly File Version : %s", assemblyInformation.getVersion()));
            listener.getLogger().println(String.format("Assembly Informational Version : %s", assemblyInformation.getInformationalVersion()));

            // For each file, replace the assembly information
            for (FilePath f : workspace.list(assemblyGlob)) {
                new ChangeTools(f, "AssemblyTitle[(]\".*\"[)]", "AssemblyTitle(\"%s\")").replace(assemblyInformation.getTitle(), listener);
                new ChangeTools(f, "AssemblyDescription[(]\".*\"[)]", "AssemblyDescription(\"%s\")").replace(assemblyInformation.getDescription(), listener);
                new ChangeTools(f, "AssemblyConfiguration[(]\".*\"[)]", "AssemblyConfiguration(\"%s\")").replace(assemblyInformation.getConfiguration(), listener);
                new ChangeTools(f, "AssemblyCompany[(]\".*\"[)]", "AssemblyCompany(\"%s\")").replace(assemblyInformation.getCompany(), listener);
                new ChangeTools(f, "AssemblyProduct[(]\".*\"[)]", "AssemblyProduct(\"%s\")").replace(assemblyInformation.getProduct(), listener);
                new ChangeTools(f, "AssemblyCopyright[(]\".*\"[)]", "AssemblyCopyright(\"%s\")").replace(assemblyInformation.getCopyright(), listener);
                new ChangeTools(f, "AssemblyTrademark[(]\".*\"[)]", "AssemblyTrademark(\"%s\")").replace(assemblyInformation.getTrademark(), listener);
                new ChangeTools(f, "AssemblyCulture[(]\".*\"[)]", "AssemblyCulture(\"%s\")").replace(assemblyInformation.getCulture(), listener);
                new ChangeTools(f, this.regexPattern, this.replacementPattern).replace(assemblyInformation.getVersion(), listener);
                new ChangeTools(f, "AssemblyInformationalVersion[(]\".*\"[)]", "AssemblyInformationalVersion(\"%s\")").replace(assemblyInformation.getInformationalVersion(), listener);
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            listener.getLogger().println(sw.toString());

            throw new AbortException(sw.toString());
        }
    }

    @Extension
    @Symbol("changeAsmVer")
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Change Assembly Version";
        }
    }

}
