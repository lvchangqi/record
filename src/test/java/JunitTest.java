import org.andios.test.AnycTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by version on 17-3-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test.xml")
public class JunitTest {

    @Autowired
    private AnycTest at;

    @Test
    public void test() throws InterruptedException {
        System.out.println("junit test ===>> 1");
        at.main();
        System.out.println("junit test ===>> 2");
        Thread.sleep(7000);

    }
}
