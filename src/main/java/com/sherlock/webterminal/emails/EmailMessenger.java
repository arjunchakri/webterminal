package com.sherlock.webterminal.emails;

import static j2html.TagCreator.*;
import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.span;
import static j2html.TagCreator.style;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.text;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import com.sherlock.webterminal.core.workspace.WorkspaceConstants;
import org.apache.commons.io.FileUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.email.EmailPopulatingBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;

import com.google.gson.Gson;
import com.sherlock.webterminal.emails.data.EmailConfig;

public class EmailMessenger {

	public final static String DIR_CONFIGURATION_JSON = WorkspaceConstants.APP_WORKSPACE + "/email/config.json";
	public final static String CSS_STRING =
			"table{border-collapse: collapse; border: 1px solid black; font-family:\"Helvetica Neue\",Helvetica,sans-serif}caption{text-align:left;color:silver;font-weight:bold;text-transform:uppercase;padding:5px}thead{background:SteelBlue;color:white}th,td{border: 1px solid black; padding:5px 10px}tbody tr:nth-child(even){background:WhiteSmoke}tbody tr td:nth-child(3), tbody tr td:nth-child(4){text-align:right;font-family:monospace}tfoot{background:SeaGreen;color:white;text-align:right}tfoot tr th:last-child{font-family:monospace}";

	public static void main(String[] args) {
		// System.out.println(checkEmailConfiguration());
		System.out.println(buildEmailContent("test", "test", "test", "test", "test"));
	}

	public static void testemail() {

		Mailer mailer = MailerBuilder.withSMTPServer("SMTP_SERVER_HERE", 25)
				.buildMailer();

		Email email = EmailBuilder.startingBlank()
				.to("Arjun Chakravarthy", "arjun@EMAILDOMAIN.com")
				.from("webterminal", "webterminal@EMAILDOMAIN.com")
				.withSubject("hey")
				.withHTMLText("<b>Test mail notif at " + System.currentTimeMillis() + "</b>")
				.buildEmail();

		mailer.sendMail(email);

	}

	public static void sendEmail(String actionId, String command, String inputVars, String executionTime, String status,
			String to) {
		try {
			String content = buildEmailContent(actionId, command, inputVars, executionTime, status);
			String subject = actionId + " || Webterminal action ";
			sendEmail(to, content, subject);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void sendEmail(String to, String content, String subject) {
		try {

			File emailConfigFile = new File(DIR_CONFIGURATION_JSON);

			if (!(emailConfigFile.exists() || emailConfigFile.canRead())) {
				return;
			}

			String configContents = FileUtils.readFileToString(emailConfigFile, Charset.defaultCharset());

			Gson gson = new Gson();
			EmailConfig emailConfig = gson.fromJson(configContents, EmailConfig.class);

			String smtp_host = emailConfig.getSmtp_host();
			String smtp_port = emailConfig.getSmtp_port();
			String smtp_username = emailConfig.getSmtp_username();
			String smtp_password = emailConfig.getSmtp_password();
			String from_address = emailConfig.getFrom_address();
			int smtp_port_num = 25;

			Mailer mailer = null;

			if (smtp_host == null || smtp_host.isEmpty()) {
				return;
			}
			if (smtp_port == null || smtp_port.isEmpty()) {
				smtp_port_num = 25;
			}
			else {
				smtp_port_num = Integer.parseInt(smtp_port);
			}
			if (smtp_username != null && !smtp_username.isEmpty() && smtp_password != null & !smtp_password.isEmpty()) {
				mailer = MailerBuilder.withSMTPServer(smtp_host, smtp_port_num, smtp_username, smtp_password)
						.buildMailer();
			}
			else {
				mailer = MailerBuilder.withSMTPServer(smtp_host, smtp_port_num)
						.buildMailer();
			}

			if (from_address == null || from_address.isEmpty()) {
				from_address = "webterminalnotifier@webterminal.app";
			}

			EmailPopulatingBuilder emailBuilder = EmailBuilder.startingBlank();

			for (String eachEmail : to.split(",")) {
				eachEmail = eachEmail.trim();
				emailBuilder.to(eachEmail, eachEmail);
			}
			Email buildEmail = emailBuilder.from(from_address)
					.withSubject(subject)
					.withHTMLText(content)
					.buildEmail();

			System.out.println("Sending mail to " + from_address + ", Content -> " + content);

			mailer.sendMail(buildEmail);

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String buildEmailContent(String actionId, String command, String inputVars, String executionTime,
			String status) {
		try {

			// <link
			// href="https://fonts.googleapis.com/css2?family=Ubuntu&display=swap"
			// rel="stylesheet">
			String statusDisplay = "OK";
			String statusColor = "green";
			if (!"0".equals(status)) {
				statusDisplay = "Failed";
				statusColor = "red";
			}

			String emailContent = html(style(CSS_STRING), body(
					h3("Hello Web terminator !! your action execution has completed."), br(), br(),
					table().with(thead().with(tr().with(th().with(span("Detail")), th().with(span("Value")))),
							tbody().with(tr().with(td().with(span("Machine: ")), td().with(span(getLocalIPHostname()))),
									tr().with(td().with(span("Action id: ")), td().with(span(actionId))),
									tr().with(td().with(span("Command: ")), td().with(span(command))),
									tr().with(td().with(span("Inputs: ")), td().with(span(inputVars))),
									tr().with(td().with(span("Execution time: ")), td().with(span(executionTime))),
									tr().with(td().with(span("Exit code: (0 is success) ")),
											td().with(span(tag("font").attr("color", statusColor)
													.with(text(statusDisplay))))))))).render();
			// emailContent = CSS_STRING + emailContent;
			return emailContent;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	private static String getLocalIPHostname() {
		StringBuilder ipAddress = new StringBuilder();
		try {
			ipAddress.append(" -- " + Inet4Address.getLocalHost()
					.getHostAddress());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ipAddress.append(" -- " + Inet4Address.getLocalHost()
					.getHostName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// FALLBACK IP
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("www.google.com", 80));
			ipAddress.append(" -- " + socket.getLocalAddress());
			socket.close();
		}
		catch (Exception innere) {
			innere.printStackTrace();
		}

		if (ipAddress.toString()
				.isEmpty()) {
			return "NOT_FOUND";
		}

		return ipAddress.toString();
	}

}
