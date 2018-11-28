import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.inspur.podm.common.persistence.entity.Chassis;
import com.inspur.podm.service.ServiceApplication;
import com.inspur.podm.service.dao.ChassisDao;
import com.inspur.podm.service.dao.MyChassisDao;

/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */

/**
 * @ClassName: LcDaoTest
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月26日 下午5:03:07
 */
@SpringBootTest(classes = ServiceApplication.class)
@RunWith(SpringRunner.class)
public class LcDaoTest {
@Autowired
ChassisDao chassisDao;
@Autowired
MyChassisDao myDao;
@Test
public void test() {
//	Chassis c = chassisDao.create();
	Chassis c = new Chassis();
	c.setDescription("lalalala");
	myDao.saveAndFlush(c);
	System.out.println("hahahahahahahaha");
}
}

