package me.xuzan.utils.schedule;

import me.xuzan.utils.comm.CommTool;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Schedule {

    //六个参数：  秒   分   时   日   月   年
//    @Scheduled(cron = "* * * * * *")//一秒执行一次
//    @Scheduled(cron = "0,6 * * * * *")//每分钟的0秒和6秒各执行一次
//    @Scheduled(cron = "0/6 * * * * *")//每6秒执行一次
    @Scheduled(cron = "0 0 9,18 ? * MON-FRI")//执行一次
    public void reminder() {
        System.out.println("执行 cron 测试：" + CommTool.getDateStr(new Date()));
    }

    //    @Scheduled(initialDelay = 8 * 1000, fixedDelay = 3 * 1000)
    public void initDelay() {
        System.out.println("初始延迟 测试：" + CommTool.getDateStr(new Date()));
    }

    //    @Scheduled(fixedRate = 2 * 1000)
    public void fixedRate() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("上次调用开始后再次调用的延时 测试：" + CommTool.getDateStr(new Date()));
    }


}
