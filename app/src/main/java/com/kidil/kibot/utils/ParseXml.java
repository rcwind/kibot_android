package com.kidil.kibot.utils;

import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseXml {
	public HashMap<String, String> parseXml (InputStream inStream) throws Exception{
		HashMap<String, String> hashMap = new HashMap<String, String>();
		//����DocumentBuilderFactory���ö��󽫴���DocumentBuilder��
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//����DocumentBuilder��DocumentBuilder��ʵ�ʽ��н����Դ���Document����
		DocumentBuilder builder = factory.newDocumentBuilder();
		//�������ļ��Դ���Document����
		Document document = builder.parse(inStream);
		//��ȡXML�ļ����ڵ� 
		Element root = document.getDocumentElement();
		//��������ӽڵ�
		NodeList childNodes = root.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = (Node) childNodes.item(i);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				//�汾�� 
				if("version".equals(childElement.getNodeName())) {
					hashMap.put("version", childElement.getFirstChild().getNodeValue());
				//������� 
				} else if("name".equals(childElement.getNodeName())) {
					hashMap.put("name", childElement.getFirstChild().getNodeValue());
				//���ص�ַ
				} else if("url".equals(childElement.getNodeName())) {
					hashMap.put("url", childElement.getFirstChild().getNodeValue());
				}
			}
			
		}
		return hashMap;
	}
}
