package com.sherlock.webterminal.formulas.data;

public class FormulaWebInput {

  private String name;
  private String output;
  private String lastmodified;
  private String exectime;
  private String frequency;

  public FormulaWebInput(String name, String output, String lastmodified, String exectime, String frequency) {
    super();
    this.name = name;
    this.output = output;
    this.lastmodified = lastmodified;
    this.exectime = exectime;
    this.frequency = frequency;
  }

  public FormulaWebInput() {
  }

  public String getName() {
    return name;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public String getExectime() {
    return exectime;
  }

  public void setExectime(String exectime) {
    this.exectime = exectime;
  }

  public String webInput() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public String getLastmodified() {
    return lastmodified;
  }

  public void setLastmodified(String lastmodified) {
    this.lastmodified = lastmodified;
  }

  @Override
  public String toString() {
    return "FormulaWebInput [name=" + name + ", output=" + output + ", lastmodified=" + lastmodified + ", exectime="
        + exectime + ", frequency=" + frequency + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((exectime == null) ? 0 : exectime.hashCode());
    result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
    result = prime * result + ((lastmodified == null) ? 0 : lastmodified.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((output == null) ? 0 : output.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FormulaWebInput other = (FormulaWebInput) obj;
    if (exectime == null) {
      if (other.exectime != null)
        return false;
    } else if (!exectime.equals(other.exectime))
      return false;
    if (frequency == null) {
      if (other.frequency != null)
        return false;
    } else if (!frequency.equals(other.frequency))
      return false;
    if (lastmodified == null) {
      if (other.lastmodified != null)
        return false;
    } else if (!lastmodified.equals(other.lastmodified))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (output == null) {
      if (other.output != null)
        return false;
    } else if (!output.equals(other.output))
      return false;
    return true;
  }

}
