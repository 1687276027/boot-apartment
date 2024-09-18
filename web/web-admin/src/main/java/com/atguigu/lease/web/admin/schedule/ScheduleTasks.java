package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleTasks {

    /*//设置定时任务
    @Scheduled(cron = "* * * * * *") //*d代表任意，cron表达式设置什么时候执行以下方法，此时为每一秒都要执行
    public void test() {
        System.out.println(new Date());
    }*/

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * *") //*d代表任意，cron表达式设置什么时候执行以下方法，此时为每天0点整执行
    public void checkLeaseStatus() {
        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.le(LeaseAgreement::getLeaseEndDate, new Date());
        updateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING);
        updateWrapper.set(LeaseAgreement::getStatus, LeaseStatus.SIGNED);

        leaseAgreementService.update(updateWrapper);
    }
}
