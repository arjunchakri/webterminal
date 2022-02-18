//package com.sherlock.nothing.commandexec.zeroturnaround;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeoutException;
//
//import org.zeroturnaround.exec.ProcessExecutor;
//import org.zeroturnaround.exec.ProcessResult;
//import org.zeroturnaround.exec.stream.LogOutputStream;
//
//public class ZeroTurnaroundTesting {
//
//  public static void main(String[] args) throws Exception {
//    try {
//
//      Map<String, String> envProps = new HashMap<String, String>();
//      envProps.put("test1", "value1");
//      envProps.put("test2", "value2");
//
//      ProcessExecutor redirectOutput = new ProcessExecutor().command("cmd", "/c", "java Formula")
//          .environment(envProps).redirectOutput(new LogOutputStream() {
//            @Override
//            protected void processLine(String line) {
//              System.out.println(line);
//            }
//          }).destroyOnExit().directory(new File(
//              "C:\\Users\\anunugonda\\Documents\\GitHub\\pw-hackathon-webterminal\\webterminal-data\\formulae\\payloadslist\\"));
//
//      ProcessResult execute = redirectOutput.execute();
//
////      ProcessExecutor redirectOutput = new ProcessExecutor()
////          .command("cmd", "/c", "java -jar installer-2.9.5.jar -nogui -silent")
////          .environment(envProps)
////          .redirectOutput(new LogOutputStream() {
////            @Override
////            protected void processLine(String line) {
////              System.out.println(line);
////            }
////          }).destroyOnExit()
////          .directory(new File("C:\\aPWInstall\\Testdownload_HB6.8.0.0_ALLGRPs\\BackupRestoretesting"));
////
////      ProcessResult execute = redirectOutput.execute();
//
//    } catch (TimeoutException e) {
//      // process is automatically destroyed
//    }
//
//  }
//
//}
