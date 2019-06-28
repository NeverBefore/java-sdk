package test.com.qiniu.sms;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.sms.SmsManager;
import com.qiniu.sms.model.SignatureInfo;
import com.qiniu.sms.model.TemplateInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test.com.qiniu.TestConfig;

import javax.swing.text.html.FormSubmitEvent.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SmsTest {
    private SmsManager smsManager;

    /**
     * 初始化
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.smsManager = new SmsManager(Auth.create(TestConfig.smsAccessKey, TestConfig.smsSecretKey));
    }

    public static boolean find(int code, int ...codes) {
        System.out.println("response code is: " + code + ", expected code is in: " + Arrays.toString(codes));
        for (int i : codes) {
            if (code == i) {
                return true;
            }
        }
        return false;
    }

    public static int[] getResCode(int ...codes) {
        return getResCode(TestConfig.isTravis(), codes);
    }

    public static int[] getResCode(boolean isTravis, int ...codes) {
        if (isTravis) {
            int[] n = new int[codes.length + 1];
            System.arraycopy(codes, 0, n, 0, codes.length);
            n[codes.length] = -1; // add code -1 for networking failed.
            return n;
        }
        return codes;
    }

    @Test
    public void testAddCode() {
        Assert.assertArrayEquals(new int[]{401, -1}, getResCode(true, 401));
        Assert.assertArrayEquals(new int[]{401}, getResCode(false, 401));
    }


    @Test
    public void testSendMessage() {
        try {
            Map<String, String> paramMap = new HashMap<String, String>();
            Response response = smsManager.sendMessage("test", new String[]{"10086"}, paramMap);
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testDescribeSignature() {
        try {
            Response response = smsManager.describeSignature("passed", 0, 0);
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testDescribeSignatureItems() {
        try {
            SignatureInfo signatureInfo = smsManager.describeSignatureItems("passed", 0, 0);
            Assert.assertNotNull(signatureInfo);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testCreateSignature() {
        try {
            Response response = smsManager.createSignature("signature", "app",
                    new String[]{"data:image/gif;base64,xxxxxxxxxx"});
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testModifySignature() {
        try {
            Response response = smsManager.modifySignature("signatureId", "signature");
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testDeleteSignature() {
        try {
            Response response = smsManager.deleteSignature("signatureId");
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testDescribeTemplate() {
        try {
            Response response = smsManager.describeTemplate("passed", 0, 0);
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testDescribeTemplateItems() {
        try {
            TemplateInfo templateInfo = smsManager.describeTemplateItems("passed", 0, 0);
            Assert.assertNotNull(templateInfo);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testCreateTemplate() {
        try {
            Response response = smsManager.createTemplate("name", "template", "notification", "desc", "signatureId");
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testModifyTemplate() {
        try {
            Response response = smsManager.modifyTemplate("templateId", "name", "template", "desc", "signatureId");
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testDeleteTemplate() {
        try {
            Response response = smsManager.deleteTemplate("templateId");
            Assert.assertNotNull(response);
        } catch (QiniuException e) {
            Assert.assertTrue(SmsTest.find(e.code(), SmsTest.getResCode(401)));
        }
    }

    @Test
    public void testComposeHeader() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class<SmsManager> clazz = SmsManager.class;
        Method declaredMethod = clazz.getDeclaredMethod("composeHeader", String.class, String.class, byte[].class,
                String.class);
        declaredMethod.setAccessible(true);
        Object invoke = declaredMethod.invoke(this.smsManager, "http://sms.qiniuapi.com",
                MethodType.GET.toString(), null, Client.DefaultMime);
        declaredMethod.setAccessible(false);
        StringMap headerMap = (StringMap) invoke;
        Assert.assertEquals("application/octet-stream", headerMap.get("Content-Type"));
        Assert.assertEquals("Qiniu test:uwduNrdHyYG9mTUFVBy8xzLg104=", headerMap.get("Authorization"));
    }

}
