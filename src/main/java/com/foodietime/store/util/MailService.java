//package com.foodietime.store.util;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//import javax.mail.*;
//import javax.mail.internet.*;
//
//public class MailService {
//
//	// 設定使用SSL連線至 Gmail smtp Server
//	private String myGmail;
//	private String myGmailPassword;
//	private Properties mailProps;
//
//	public MailService() throws IOException {
//		mailProps = new Properties();
//		mailProps.put("mail.smtp.host", "smtp.gmail.com");
//		mailProps.put("mail.smtp.socketFactory.port", "465");
//		mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		mailProps.put("mail.smtp.auth", "true");
//		mailProps.put("mail.smtp.port", "465");
//
//		// ●設定 gmail 的帳號 & 密碼 (將藉由你的Gmail來傳送Email)
//		Properties prop = new Properties();
//		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties");
//		prop.load(input);
//		myGmail = prop.getProperty("mail.username");
//		myGmailPassword = prop.getProperty("mail.password");
//	}
//
//	public void sendMail(String to, String subject, String htmlContent) throws IOException {
//
//		try {
//
//			Session session = Session.getInstance(mailProps, new Authenticator() {
//				protected PasswordAuthentication getPasswordAuthentication() {
//					return new PasswordAuthentication(myGmail, myGmailPassword);
//				}
//			});
//
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(myGmail));
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//			message.setSubject(subject);
//			message.setContent(htmlContent, "text/html; charset=utf-8");
//
//			Transport.send(message);
//			System.out.println("傳送成功!");
//		} catch (MessagingException e) {
//			System.out.println("傳送失敗!");
//			e.printStackTrace();
//		}
//	}
//
//	//main方法測試
////	public static void main(String args[]) {
////
////		String to = "meigo1229@gmail.com";
////
////		String subject = "密碼通知測試喔!";
////
////		String ch_name = "店家";
////		String passRandom = ReturnAuthCode.returnAuthCode();
////
////		String loginLink = "http://localhost:8081/CJA10140-Webapp/store/verifymail.do";
////
////		String htmlContent = "<html><body>" + "<h3>Hello " + ch_name + "，您的密碼如下：</h3>" + "<p><strong>" + passRandom
////				+ "</strong></p>" + "<p>此密碼已啟用，請妥善保管。</p>" + "<sapn>請點選以下連結登入系統：</sapn>" + "<a href='" + loginLink
////				+ "'>👉 點我登入</a>" + "</body></html>";
////
////		try {
////			MailService mailService = new MailService();
////			mailService.sendMail(to, subject, htmlContent);
////		} catch (IOException e) {
////			e.printStackTrace(); // 處理 mail.properties 讀不到的錯誤
////		}
////	}
//}
