package com.sparrow.switcher;

import com.sparrow.annotation.AppSwitch;
import com.sparrow.config.Constants;
import com.sparrow.config.SparrowProperties;
import com.sparrow.exception.SparrowException;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Properties;
import java.util.Scanner;

/**
 * @author 985492783@qq.com
 * @date 2024/6/23 22:19
 */
@RunWith(JUnit4.class)
public class SparrowSwitcherServiceTest {

    @Test
    public void testGrpc() throws SparrowException {
        SparrowProperties properties = new SparrowProperties();
        SparrowSwitcherService service = new SparrowSwitcherService(properties);
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }
    @Test
    public void testProperties() throws SparrowException {
        Properties properties = System.getProperties();
        properties.put(Constants.SPARROW_APP_NAME, "test");
        properties.put(Constants.SPARROW_NAMESPACE, "public");
        properties.put(Constants.SPARROW_GRPC_ADDR, "127.0.0.1:9854");
        SwitcherManager.initManager(properties);
        Node node = new Node();
        SwitcherManager.init(node.getClass());
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

    public static class Node {
        @AppSwitch(desc = "姓名")
        public static String name = "sparrow";
        @AppSwitch(desc = "端口号")
        public static int port = 8080;
        @AppSwitch(desc = "是否开启")
        public static boolean start = true;
        @AppSwitch(desc = "项")
        public static Item item = new Item();
    }

    @Data
    public static class Item {
        private String key = "hahah";
        private String value = "bbb";

    }
}
