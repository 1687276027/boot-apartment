package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.app.mapper.GraphInfoMapper;
import com.atguigu.lease.web.app.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.LabelInfoService;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Autowired
    private ViewAppointmentMapper mapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;


    @Override
    public List<AppointmentItemVo> listItem() {
        return mapper.listItem(LoginUserHolder.getLoginUser().getUserId());
    }

    @Override
    public AppointmentDetailVo getDetailById(Long id) {
        AppointmentDetailVo appointmentDetailVo = new AppointmentDetailVo();
        ViewAppointment viewAppointment = mapper.selectById(id);
        ApartmentDetailVo apartmentDetailVo = apartmentInfoService.getDetailById(viewAppointment.getApartmentId());
        BeanUtils.copyProperties(viewAppointment, appointmentDetailVo);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(apartmentDetailVo, apartmentItemVo);

        appointmentDetailVo.setApartmentItemVo(apartmentItemVo);
        return appointmentDetailVo;
    }
}



