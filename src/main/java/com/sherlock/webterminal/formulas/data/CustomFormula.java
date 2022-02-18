package com.sherlock.webterminal.formulas.data;

public class CustomFormula {

  private int frequency;
  private String workspace;
  private String sourceCode;
  private String name;
  private Class classObject;
  private String compilationIssues = null;

  public CustomFormula(int frequency, String workspace, String sourceCode, String name, Class classObject) {
    super();
    this.frequency = frequency;
    this.workspace = workspace;
    this.sourceCode = sourceCode;
    this.name = name;
    this.classObject = classObject;
    compilationIssues = null;
  }

  public String getCompilationIssues() {
    return compilationIssues;
  }

  public void setCompilationIssues(String compilationIssues) {
    this.compilationIssues = compilationIssues;
  }

  public Class getClassObject() {
    return classObject;
  }

  public void setClassObject(Class classObject) {
    this.classObject = classObject;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public String getWorkspace() {
    return workspace;
  }

  public void setWorkspace(String workspace) {
    this.workspace = workspace;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  @Override
  public String toString() {
    return "CustomFormula [frequency=" + frequency + ", workspace=" + workspace + ", sourceCode=" + sourceCode
        + ", name=" + name + ", classObject=" + classObject + ", compilationIssues=" + compilationIssues + "]";
  }

}
