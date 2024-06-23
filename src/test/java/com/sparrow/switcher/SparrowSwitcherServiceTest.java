package com.sparrow.switcher;

import com.sparrow.config.SparrowProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Scanner;

/**
 * @author 985492783@qq.com
 * @date 2024/6/23 22:19
 */
@RunWith(JUnit4.class)
public class SparrowSwitcherServiceTest {

    @Test
    public void testGrpc() {
        SparrowProperties properties = new SparrowProperties();
        SparrowSwitcherService service = new SparrowSwitcherService(properties);
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

}
