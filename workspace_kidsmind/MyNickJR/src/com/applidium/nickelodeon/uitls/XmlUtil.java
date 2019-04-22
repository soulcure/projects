package com.applidium.nickelodeon.uitls;


import android.util.Xml;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * 生成xml字符串
 */
public class XmlUtil {

    public static String xmlGen(String tag, List<NameValuePair> bodys) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("utf-8", null);

            serializer.startTag(null, tag);

            if (bodys != null) {
                for (NameValuePair headerPair : bodys) {

                    serializer.startTag(null, headerPair.getName());
                    serializer.text(headerPair.getValue());
                    serializer.endTag(null, headerPair.getName());

                }
            }

            serializer.endTag(null, tag);
            serializer.endDocument();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }


    public static String xmlLogin(String token, String app_id, String account_id,
                                  String password) {
        String xml = "<login>";
        xml = xml + xmlRepresentation("tokenType", "null");
        xml = xml + xmlRepresentation("token", token);
        xml = xml + xmlRepresentation("app_id", app_id);
        xml = xml + xmlRepresentation("account_id", account_id);
        xml = xml + xmlRepresentation("password", password);
        xml = xml + "</login>";
        return xml;
    }


    public static String xmlRepresentation(String attribute, String value) {
        if (value != null) {
            return String.format("<%s>%s</%s>", attribute, value, attribute);
        } else {
            return "";
        }
    }
}
