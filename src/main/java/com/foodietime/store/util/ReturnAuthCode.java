package com.foodietime.store.util;

public class ReturnAuthCode {

//	public static void main(String[] args) {
//		String sb = returnAuthCode();
//		System.out.println(sb);
//	}

	public static String returnAuthCode() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 8; i++) {
			int condition = (int) (Math.random() * 3) + 1; // 隨機3選1
			switch (condition) {
			case 1:
				char c1 = (char) ((int) (Math.random() * 26) + 65);// A-Z
				sb.append(c1);
				break;

			case 2:
				char c2 = (char) ((int) (Math.random() * 26) + 97);// a-z
				sb.append(c2);
				break;

			case 3:
				sb.append((int) (Math.random() * 10));// 0-9

				break;

			}
		}

		return sb.toString();

	}
}
