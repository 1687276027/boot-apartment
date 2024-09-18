package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.j2objc.annotations.AutoreleasePool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;


    @Autowired
    private UserInfoService userInfoService;


    @Override
    public List<AgreementItemVo> listItem() {
        UserInfo userInfo = userInfoService.getById(LoginUserHolder.getLoginUser().getUserId());
        return leaseAgreementMapper.listItem(userInfo.getPhone());
    }

    @Override
    public AgreementDetailVo getDetailById(Long id) {

        // 1.租约信息
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);

        if (leaseAgreement == null) {
            return null;
        }
        // 2.公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());
        // 3.房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());
        // 4.付款方式
        PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());
        // 5.租期信息
        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());
        // 6.图片列表
        List<GraphVo> roomGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, roomInfo.getId());
        List<GraphVo> apartmentGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());

        // 7.组装
        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(leaseAgreement, agreementDetailVo);
        agreementDetailVo.setApartmentName(apartmentInfo.getName());
        agreementDetailVo.setRoomNumber(roomInfo.getRoomNumber());
        agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
        agreementDetailVo.setPaymentTypeName(paymentType.getName());
        agreementDetailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
        agreementDetailVo.setLeaseTermUnit(leaseTerm.getUnit());

        return agreementDetailVo;
    }

    @Override
    public List<PaymentType> listByRoomId(Long id) {
        return paymentTypeMapper.selectListByRoomId(id);
    }

    @Override
    public List<LeaseTerm> listLeaeTermByRoomId(Long id) {
        return leaseTermMapper.selectListByRoomId(id);
    }
}




