package com.sherlock.webterminal.formulas;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.sherlock.webterminal.formulas.data.CustomFormula;

public class CronService extends TimerTask {

  private CustomFormula formula;

  public CronService(CustomFormula serviceName) {
    super();
    this.formula = serviceName;

  }

  public void startCron(Timer timer) {
    try {
      long delay = formula.getFrequency() * 1000; // <seconds> * 1000
      timer.scheduleAtFixedRate(this, new Date(), delay);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("CronService init error for " + formula);
    }
  }

  @Override
  public void run() {
    CustomFormulaExecutor.handleCronEvent(formula);
  }

}
