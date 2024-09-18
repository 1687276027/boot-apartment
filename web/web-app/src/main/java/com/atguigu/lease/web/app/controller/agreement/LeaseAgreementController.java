package com.atguigu.lease.web.app.controller.agreement;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.LeaseTerm;
import com.atguigu.lease.model.entity.PaymentType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.LeaseAgreementMapper;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/agreement")
@Tag(name = "租约信息")
public class LeaseAgreementController {

    @Autowired
    private LeaseAgreementService leaseAgreementService;


    @Operation(summary = "获取个人租约基本信息列表")
    @GetMapping("listItem")
    public Result<List<AgreementItemVo>> listItem() {
        List<AgreementItemVo> result = leaseAgreementService.listItem();
        return Result.ok(result);
    }

    @Operation(summary = "根据id获取租约详细信息")
    @GetMapping("getDetailById")
    public Result<AgreementDetailVo> getDetailById(@RequestParam Long id) {
        AgreementDetailVo result = leaseAgreementService.getDetailById(id);
        return Result.ok(result);
    }

    @Operation(summary = "根据id更新租约状态", description = "用于确认租约和提前退租")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam LeaseStatus leaseStatus) {
        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseAgreement::getId, id);
        updateWrapper.set(LeaseAgreement::getStatus, leaseStatus);
        leaseAgreementService.update(updateWrapper);
        return Result.ok();
    }

    @Operation(summary = "保存或更新租约", description = "用于续约")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody LeaseAgreement leaseAgreement) {
        leaseAgreementService.saveOrUpdate(leaseAgreement);
        return Result.ok();
    }

    @Operation(summary = "根据房间id获取可选支付方式列表")
    @GetMapping("listPaymentTypeByRoomId")
    public Result<List<PaymentType>> listByPaymentType(@RequestParam Long id) {
        List<PaymentType> list =  leaseAgreementService.listByRoomId(id);
        return Result.ok(list);
    }

    @GetMapping("listLeaseTermByRoomId")
    @Operation(summary = "根据房间id获取可选获取租期列表")
    public Result<List<LeaseTerm>> listByLeaseTerm(@RequestParam Long id) {
        List<LeaseTerm> list = leaseAgreementService.listLeaeTermByRoomId(id);
        return Result.ok(list);
    }

}
