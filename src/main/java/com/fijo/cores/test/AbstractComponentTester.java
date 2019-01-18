package com.fijo.cores.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zhangbo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:conf/applicationContext-resources.xml",
		"classpath:conf/applicationContext-dao.xml",
		"classpath*:conf/applicationContext-cores.xml",
		"classpath:conf/applicationContext.xml"})
public class AbstractComponentTester extends AbstractJUnit4SpringContextTests {
	
}
